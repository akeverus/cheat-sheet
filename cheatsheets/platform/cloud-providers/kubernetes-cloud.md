---
title: "Kubernetes в облаке"
description: "Kubernetes становится стандартом для оркестрации контейнеров в облачных средах. Этот документ охватывает managed Kubernetes сервисы (EKS, AKS, GKE), best practices для cloud-native deployments, multi-cloud стратегии и интеграцию с облачными сервисами. Документ дополняет [Kubernet"
tags:
  - platform
  - cloud-providers
  - kubernetes-cloud
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kubernetes в облаке

**Kubernetes** становится стандартом для оркестрации контейнеров в облачных средах. Этот документ охватывает **managed Kubernetes** сервисы (EKS, `AKS`, GKE), **best practices** для **cloud-native deployments**, **multi-cloud** стратегии и интеграцию с облачными сервисами. Документ дополняет [[kubernetes-advanced|Kubernetes Advanced]] фокусом на облачные аспекты.

## Полезные ссылки
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Azure AKS Documentation](https://docs.microsoft.com/azure/aks/)
- [Google GKE Documentation](https://cloud.google.com/kubernetes-engine/docs)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Istio Documentation](https://istio.io/latest/docs/)
- [Flux CD](https://fluxcd.io/docs/)
- [Argo Rollouts](https://argoproj.github.io/rollouts/)

## Содержание

- [AWS EKS (Elastic Kubernetes Service)](#aws-eks-elastic-kubernetes-service)
  - [EKS Cluster Architecture](#eks-cluster-architecture)
  - [EKS Node Groups](#eks-node-groups)
  - [EKS Networking](#eks-networking)
  - [EKS Monitoring и Logging](#eks-monitoring-и-logging)
- [Azure AKS (Azure Kubernetes Service)](#azure-aks-azure-kubernetes-service)
  - [AKS Cluster Configuration](#aks-cluster-configuration)
  - [AKS Advanced Networking](#aks-advanced-networking)
  - [AKS Security Features](#aks-security-features)
- [Google GKE (Google Kubernetes Engine)](#google-gke-google-kubernetes-engine)
  - [GKE Autopilot](#gke-autopilot)
  - [GKE Standard with Advanced Features](#gke-standard-with-advanced-features)
  - [GKE Networking Features](#gke-networking-features)
- [Multi-Cloud Kubernetes](#multi-cloud-kubernetes)
  - [Anthos (Google Cloud)](#anthos-google-cloud)
  - [Crossplane для Multi-Cloud](#crossplane-для-multi-cloud)
  - [Cluster API](#cluster-api)
- [Cloud-Native Patterns](#cloud-native-patterns)
  - [GitOps with Flux](#gitops-with-flux)
  - [Progressive Delivery](#progressive-delivery)
  - [Infrastructure as Code](#infrastructure-as-code)
- [Service Mesh Integration](#service-mesh-integration)
  - [Istio on Cloud Kubernetes](#istio-on-cloud-kubernetes)
  - [Linkerd Service Mesh](#linkerd-service-mesh)
- [Security в Cloud Kubernetes](#security-в-cloud-kubernetes)
  - [Pod Security Standards](#pod-security-standards)
  - [Network Policies](#network-policies)
  - [Secrets Management](#secrets-management)
- [Cost Optimization](#cost-optimization)
  - [Cluster Autoscaling](#cluster-autoscaling)
  - [Spot Instances и Preemptibles](#spot-instances-и-preemptibles)
  - [Resource Optimization](#resource-optimization)
  - [Cost Monitoring](#cost-monitoring)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## AWS EKS (`Elastic Kubernetes Service`)

### EKS Cluster Architecture
Пример конфигурации кластера **EKS** (YAML).
```yaml
# Конфигурация кластера EKS — VPC, подсети, логирование, шифрование, аддоны (vpc-cni, coredns, kube-proxy)
eks-cluster:
  name: "eks-prod-cluster"
  version: "1.24"
  roleArn: "arn:aws:iam::account:role/eks-service-role"

  resourcesVpcConfig:
    subnetIds:
      - "subnet-private-a"
      - "subnet-private-b"
      - "subnet-private-c"
    securityGroupIds:
      - "sg-eks-cluster"
    endpointPublicAccess: false
    endpointPrivateAccess: true
    publicAccessCidrs:
      - "10.0.0.0/8"

  logging:
    clusterLogging:
      - types: ["api", "audit", "authenticator", "controllerManager", "scheduler"]
        enabled: true

  encryptionConfig:
    - resources: ["secrets"]
      provider:
        keyArn: "arn:aws:kms:region:account:key/key-id"

  kubernetesNetworkConfig:
    serviceIpv4Cidr: "10.100.0.0/16"
    ipFamily: "ipv4"

  addons:
    - name: "vpc-cni"
      version: "v1.12.0-eksbuild.1"
      configurationValues: |
        {
          "env": {
            "ENABLE_IPv6": "false"
          }
        }
    - name: "coredns"
      version: "v1.9.3-eksbuild.2"
    - name: "kube-proxy"
      version: "v1.24.7-eksbuild.2"
```

### EKS Node Groups
```yaml
# Managed Node Group
managed-node-group:
  name: "ng-app-nodes"
  clusterName: "eks-prod-cluster"
  nodeRole: "arn:aws:iam::account:role/eks-node-role"
  subnets:
    - "subnet-private-a"
    - "subnet-private-b"
    - "subnet-private-c"

  scalingConfig:
    minSize: 3
    maxSize: 10
    desiredSize: 5

  instanceTypes:
    - "t3.large"
    - "t3.xlarge"

  amiType: "AL2_x86_64"
  releaseVersion: "1.24.7-20230322"

  updateConfig:
    maxUnavailablePercentage: 25

  labels:
    environment: "production"
    node-type: "application"

  taints:
    - key: "dedicated"
      value: "app"
      effect: "NoSchedule"

  launchTemplate:
    name: "lt-eks-nodes"
    version: "$Latest"

# Fargate Profile
fargate-profile:
  name: "fp-dev-workloads"
  clusterName: "eks-prod-cluster"
  podExecutionRoleArn: "arn:aws:iam::account:role/eks-fargate-role"

  subnets:
    - "subnet-private-a"
    - "subnet-private-b"

  selectors:
    - namespace: "development"
    - namespace: "staging"
```

### EKS Networking
```yaml
# VPC CNI Configuration
vpc-cni-config:
  env:
    - name: AWS_VPC_K8S_CNI_LOGLEVEL
      value: "DEBUG"
    - name: AWS_VPC_K8S_CNI_LOG_FILE
      value: "/host/var/log/aws-routed-eni/ipamd.log"
    - name: AWS_VPC_K8S_CNI_RANDOMIZESNAT
      value: "prng"
    - name: AWS_VPC_ENI_MTU
      value: "9001"
    - name: AWS_VPC_K8S_CNI_CUSTOM_NETWORK_CFG
      value: "false"
    - name: AWS_VPC_K8S_CNI_VETHPREFIX
      value: "eni"
    - name: ENI_CONFIG_LABEL_DEF
      value: "failure-domain.beta.kubernetes.io/zone"

# Security Groups for Pods
pod-security-groups:
  securityGroupPolicy:
    name: "pod-sg-policy"
    spec:
      podSelector:
        matchLabels:
          app: "secure-app"
      securityGroups:
        groups:
          - "sg-secure-app"

# AWS Load Balancer Controller
alb-controller:
  clusterName: "eks-prod-cluster"
  serviceAccount:
    create: true
    name: "aws-load-balancer-controller"
    annotations:
      eks.amazonaws.com/role-arn: "arn:aws:iam::account:role/alb-controller-role"

  ingressClassConfig:
    - name: "alb"
      default: true
      controller: "ingress.k8s.aws/alb"
      parameters:
        - name: "group.name"
          value: "k8s-alb"
        - name: "group.order"
          value: "10"
```

### EKS Monitoring и Logging
```yaml
# CloudWatch Container Insights
container-insights:
  clusterName: "eks-prod-cluster"
  accountId: "account"
  region: "us-east-1"

  # Fluent Bit configuration for log shipping
  fluent-bit:
    enabled: true
    namespace: "amazon-cloudwatch"
    serviceAccount:
      create: true
      name: "cloudwatch-agent"
      annotations:
        eks.amazonaws.com/role-arn: "arn:aws:iam::account:role/cloudwatch-agent-role"

# X-Ray Integration
xray-daemon:
  serviceAccount:
    name: "xray-daemon"
    annotations:
      eks.amazonaws.com/role-arn: "arn:aws:iam::account:role/xray-daemon-role"

  configMap:
    name: "xray-config"
    data:
      cfg.yaml: |
        TotalBufferSizeMB: 24
        Socket:
          UdpAddress: "0.0.0.0:2000"
          TcpAddress: "0.0.0.0:2000"
        Logging:
          LogLevel: "prod"
```

## Azure AKS (`Azure Kubernetes Service`)

### AKS Cluster Configuration
```yaml
# AKS Cluster
aks-cluster:
  name: "aks-prod-cluster"
  location: "East US 2"
  resourceGroup: "rg-aks-prod"
  kubernetesVersion: "1.24.6"
  dnsPrefix: "aks-prod"
  nodeResourceGroup: "MC_rg-aks-prod_aks-prod-cluster_eastus2"

  agentPoolProfiles:
    - name: "systempool"
      mode: "System"
      osType: "Linux"
      osSKU: "Ubuntu"
      count: 3
      vmSize: "Standard_DS2_v2"
      osDiskSizeGB: 128
      osDiskType: "Managed"
      enableAutoScaling: true
      minCount: 3
      maxCount: 5
      availabilityZones: ["1", "2", "3"]
      enableNodePublicIP: false
      scaleSetPriority: "Regular"
      spotMaxPrice: -1
      nodeLabels:
        environment: "production"
        pool: "system"
      nodeTaints: []

  apiServerAccessProfile:
    enablePrivateCluster: true
    privateDNSZone: "system"
    enablePrivateClusterPublicFQDN: false

  networkProfile:
    networkPlugin: "azure"
    networkPolicy: "azure"
    loadBalancerSku: "standard"
    outboundType: "loadBalancer"
    serviceCidr: "10.0.0.0/16"
    dnsServiceIP: "10.0.0.10"
    dockerBridgeCidr: "172.17.0.1/16"

  autoUpgradeProfile:
    upgradeChannel: "stable"

  securityProfile:
    azureKeyVaultKms:
      enabled: true
      keyId: "https://mykeyvault.vault.azure.net/keys/mykey"
      keyVaultNetworkAccess: "Private"
      keyVaultResourceId: "/subscriptions/.../resourceGroups/rg-kv/providers/Microsoft.KeyVault/vaults/mykeyvault"
```

### AKS Advanced Networking
```yaml
# Azure CNI Overlay
azure-cni-overlay:
  networkPlugin: "azure"
  networkPluginMode: "overlay"
  podCidr: "10.244.0.0/16"
  serviceCidr: "10.0.0.0/16"
  dnsServiceIP: "10.0.0.10"
  dockerBridgeCidr: "172.17.0.1/16"

# Application Gateway Ingress Controller
agic-config:
  appgw:
    subscriptionId: "subscription-id"
    resourceGroup: "rg-agw"
    name: "agw-prod"
    usePrivateIP: false
    shared: false

  rbac:
    enabled: true

  serviceAccount:
    create: true
    name: "agic-sa"
    annotations:
      azure.workload.identity/client-id: "client-id"

# Azure Front Door Integration
afd-integration:
  ingressClass: "azure/front-door"
  annotations:
    appgw.ingress.kubernetes.io/appgw-ssl-redirect: "true"
    appgw.ingress.kubernetes.io/ssl-redirect: "true"
    appgw.ingress.kubernetes.io/use-private-ip: "false"
```

### AKS Security Features
```yaml
# Azure Policy for AKS
azure-policy:
  enabled: true
  version: "v2"

# Azure AD Integration
aad-integration:
  managed: true
  enableAzureRBAC: true
  adminGroupObjectIDs:
    - "admin-group-object-id"
  tenantID: "tenant-id"

# Workload Identity
workload-identity:
  enabled: true
  serviceAccountTokenIssuer:
    enabled: true

# Azure Key Vault Provider
akv-provider:
  enabled: true
  tenantId: "tenant-id"
  clientId: "client-id"
  keyvaultName: "mykeyvault"
  objects:
    - objectName: "mysecret"
      objectType: "secret"
      objectVersion: ""
    - objectName: "mykey"
      objectType: "key"
      objectVersion: ""
```

## Google GKE (`Google Kubernetes Engine`)

### GKE Autopilot
```yaml
# GKE Autopilot Cluster
gke-autopilot-cluster:
  name: "gke-autopilot-prod"
  location: "us-central1"
  enable_autopilot: true
  network: "projects/my-project/global/networks/vpc-prod"
  subnetwork: "projects/my-project/regions/us-central1/subnetworks/subnet-app"
  ip_allocation_policy:
    cluster_secondary_range_name: "pods"
    services_secondary_range_name: "services"
  private_cluster_config:
    enable_private_nodes: true
    enable_private_endpoint: false
    master_ipv4_cidr_block: "172.16.0.32/28"
  master_authorized_networks_config:
    enabled: true
    cidr_blocks:
      - cidr_block: "10.0.0.0/8"
        display_name: "VPC Network"
      - cidr_block: "192.168.0.0/16"
        display_name: "Office Network"
  release_channel:
    channel: "REGULAR"
  addons_config:
    http_load_balancing:
      disabled: false
    horizontal_pod_autoscaling:
      disabled: false
    gcp_filestore_csi_driver_config:
      enabled: true
  database_encryption:
    state: "ENCRYPTED"
    key_name: "projects/my-project/locations/us-central1/keyRings/my-keyring/cryptoKeys/my-key"
```

### GKE Standard with Advanced Features
```yaml
# GKE Standard Cluster
gke-standard-cluster:
  name: "gke-standard-prod"
  location: "us-central1"
  initial_cluster_version: "1.24.5-gke.600"
  network: "projects/my-project/global/networks/vpc-prod"
  subnetwork: "projects/my-project/regions/us-central1/subnetworks/subnet-app"
  enable_kubernetes_alpha: false
  logging_service: "logging.googleapis.com/kubernetes"
  monitoring_service: "monitoring.googleapis.com/kubernetes"
  ip_allocation_policy:
    cluster_secondary_range_name: "pods"
    services_secondary_range_name: "services"
  private_cluster_config:
    enable_private_nodes: true
    enable_private_endpoint: false
    master_ipv4_cidr_block: "172.16.0.32/28"
  binary_authorization:
    evaluation_mode: "PROJECT_SINGLETON_POLICY_ENFORCE"
  network_policy:
    enabled: true
    provider: "CALICO"
  maintenance_policy:
    window:
      recurring_window:
        window:
          start_time: "2023-01-01T02:00:00Z"
          end_time: "2023-01-01T06:00:00Z"
        recurrence: "FREQ=WEEKLY;BYDAY=SA"
  node_pools:
    - name: "system-pool"
      initial_node_count: 3
      config:
        machine_type: "e2-medium"
        disk_size_gb: 100
        disk_type: "pd-standard"
        oauth_scopes:
          - "https://www.googleapis.com/auth/cloud-platform"
        service_account: "gke-system@my-project.iam.gserviceaccount.com"
        metadata:
          disable-legacy-endpoints: "true"
        labels:
          node-pool: "system"
        tags: ["gke-system"]
      management:
        auto_repair: true
        auto_upgrade: true
      upgrade_settings:
        max_surge: 1
        max_unavailable: 0
      autoscaling:
        enabled: true
        min_node_count: 3
        max_node_count: 5
```

### GKE Networking Features
```yaml
# GKE Gateway API
gateway-api:
  enabled: true
  gatewayClass: "gke-l7-rilb"
  gateway:
    name: "external-gateway"
    listeners:
      - name: "http"
        hostname: "*.myapp.com"
        port: 80
        protocol: "HTTP"

# Cloud Service Mesh
service-mesh:
  enabled: true
  controlPlane: "AUTOMATIC"
  securityConfig:
    mtls:
      status: "STRICT"

# GKE Backup for VMware
backup-config:
  name: "gke-backup-prod"
  location: "us-central1"
  backupConfig:
    includeVolumeData: true
    includeSecrets: true
    encryptionKey:
      gcpKmsEncryptionKey: "projects/my-project/locations/us-central1/keyRings/my-keyring/cryptoKeys/my-key"
  backupSchedule:
    cronSchedule: "0 2 * * *"
    timeZone: "UTC"
    nextRunTime: "2023-01-02T02:00:00Z"
    paused: false
```

## Multi-Cloud Kubernetes

### Anthos (Google Cloud)
```yaml
# Anthos Cluster on AWS
anthos-aws-cluster:
  name: "anthos-aws-prod"
  region: "us-east-1"
  version: "1.24.5-gke.600"
  network:
    vpc_id: "vpc-aws-prod"
    subnet_ids:
      - "subnet-aws-a"
      - "subnet-aws-b"
    security_group_ids:
      - "sg-anthos"

  control_plane:
    instance_type: "t3.large"
    aws_service_account: "anthos-control-plane@my-project.iam.gserviceaccount.com"

  node_pools:
    - name: "default-pool"
      instance_type: "t3.medium"
      min_node_count: 3
      max_node_count: 10
      aws_service_account: "anthos-nodes@my-project.iam.gserviceaccount.com"

# Anthos Config Management
config-management:
  enabled: true
  config_sync:
    source_format: "hierarchy"
    git:
      repo: "https://github.com/myorg/anthos-config"
      branch: "main"
      dir: "clusters/"
      secret_type: "ssh"
  policy_controller:
    enabled: true
    audit_interval_seconds: 60
    exemptable_namespaces: ["kube-system"]
```

### Crossplane для Multi-Cloud
```yaml
# Crossplane Configuration
crossplane-config:
  namespace: "crossplane-system"

  providers:
    - name: "aws-provider"
      package: "crossplane/provider-aws:v0.33.0"
    - name: "azure-provider"
      package: "crossplane/provider-azure:v0.20.0"
    - name: "gcp-provider"
      package: "crossplane/provider-gcp:v0.22.0"

# Multi-Cloud Kubernetes Cluster Claim
cluster-claim:
  apiVersion: "cluster.x-k8s.io/v1beta1"
  kind: "Cluster"
  metadata:
    name: "multi-cloud-cluster"
  spec:
    infrastructureRef:
      apiVersion: "infrastructure.cluster.x-k8s.io/v1beta1"
      kind: "AWSCluster"
      name: "aws-cluster"
    controlPlaneRef:
      apiVersion: "controlplane.cluster.x-k8s.io/v1beta1"
      kind: "KubeadmControlPlane"
      name: "control-plane"
```

### Cluster API
```yaml
# Cluster API AWS
cluster-api-aws:
  apiVersion: "infrastructure.cluster.x-k8s.io/v1beta1"
  kind: "AWSCluster"
  metadata:
    name: "capi-aws-cluster"
  spec:
    region: "us-east-1"
    sshKeyName: "my-ssh-key"
    controlPlaneEndpoint:
      host: ""
      port: 6443
    networkSpec:
      vpc:
        id: "vpc-capi"
      subnets:
        - id: "subnet-capi-a"
        - id: "subnet-capi-b"

# Cluster API Azure
cluster-api-azure:
  apiVersion: "infrastructure.cluster.x-k8s.io/v1beta1"
  kind: "AzureCluster"
  metadata:
    name: "capi-azure-cluster"
  spec:
    location: "East US 2"
    resourceGroup: "rg-capi"
    subscriptionID: "subscription-id"
    networkSpec:
      vnet:
        name: "vnet-capi"
      subnets:
        - name: "subnet-capi"
          role: "node"
    controlPlaneEndpoint:
      host: ""
      port: 6443
```

## Cloud-Native Patterns

### GitOps with Flux
```yaml
# Flux Installation
flux-install:
  components:
    - source-controller
    - kustomize-controller
    - helm-controller
    - notification-controller
  cluster-domain: "cluster.local"
  image-automation-controller: false
  image-reflector-controller: false
  notification-controller: false

# GitRepository Source
git-source:
  apiVersion: "source.toolkit.fluxcd.io/v1beta2"
  kind: "GitRepository"
  metadata:
    name: "flux-system"
    namespace: "flux-system"
  spec:
    interval: "1m0s"
    ref:
      branch: "main"
    secretRef:
      name: "flux-system"
    url: "https://github.com/myorg/kubernetes-config"

# Kustomization
kustomization:
  apiVersion: "kustomize.toolkit.fluxcd.io/v1beta2"
  kind: "Kustomization"
  metadata:
    name: "apps"
    namespace: "flux-system"
  spec:
    interval: "10m0s"
    path: "./apps/"
    prune: true
    sourceRef:
      kind: "GitRepository"
      name: "flux-system"
    validation: "client"
```

### Progressive Delivery
```yaml
# Argo Rollouts for Progressive Delivery
argo-rollouts:
  apiVersion: "argoproj.io/v1alpha1"
  kind: "Rollout"
  metadata:
    name: "my-app-rollout"
  spec:
    replicas: 5
    strategy:
      blueGreen:
        activeService: "my-app-active"
        previewService: "my-app-preview"
        autoPromotionEnabled: false
    selector:
      matchLabels:
        app: "my-app"
    template:
      metadata:
        labels:
          app: "my-app"
      spec:
        containers:
          - name: "my-app"
            image: "my-app:{{VERSION}}"
            ports:
              - containerPort: 8080

# Flagger for Progressive Delivery
flagger-config:
  apiVersion: "flagger.app/v1beta1"
  kind: "Canary"
  metadata:
    name: "my-app"
  spec:
    targetRef:
      apiVersion: "apps/v1"
      kind: "Deployment"
      name: "my-app"
    progressDeadlineSeconds: 60
    service:
      port: 80
      targetPort: 8080
    analysis:
      interval: "1m"
      threshold: 5
      maxWeight: 50
      stepWeight: 10
      metrics:
        - name: "request-success-rate"
          threshold: 99
          interval: "1m"
        - name: "request-duration"
          threshold: 500
          interval: "1m"
      webhooks:
        - name: "load-test"
          type: "confirm-rollout"
          url: "http://flagger-loadtester.test/"
          timeout: "5m"
```

### Infrastructure as Code
```yaml
# Terraform for Kubernetes Resources
terraform-k8s:
  terraform:
    required_providers:
      kubernetes:
        source: "hashicorp/kubernetes"
        version: "~> 2.0"

  provider:
    kubernetes:
      config_path: "~/.kube/config"

  resources:
    kubernetes_namespace:
      prod:
        metadata:
          name: "production"

    kubernetes_deployment:
      app:
        metadata:
          name: "my-app"
          namespace: "production"
        spec:
          replicas: 3
          selector:
            match_labels:
              app: "my-app"
          template:
            metadata:
              labels:
                app: "my-app"
            spec:
              containers:
                - name: "my-app"
                  image: "my-app:v1.0.0"
                  ports:
                    - container_port: 8080

# Crossplane for Cloud Resources
crossplane-resources:
  apiVersion: "compute.aws.crossplane.io/v1beta1"
  kind: "VPC"
  metadata:
    name: "k8s-vpc"
  spec:
    forProvider:
      region: "us-east-1"
      cidrBlock: "10.0.0.0/16"
      enableDnsHostnames: true
      enableDnsSupport: true
    providerConfigRef:
      name: "aws-provider"

  apiVersion: "eks.aws.crossplane.io/v1beta1"
  kind: "Cluster"
  metadata:
    name: "k8s-cluster"
  spec:
    forProvider:
      region: "us-east-1"
      version: "1.24"
      resourcesVpcConfig:
        subnetIds:
          - "subnet-12345"
          - "subnet-67890"
    providerConfigRef:
      name: "aws-provider"
```

## Service Mesh Integration

### Istio on Cloud Kubernetes
```yaml
# Istio Installation with Istio Operator
istio-operator:
  apiVersion: "install.istio.io/v1alpha1"
  kind: "IstioOperator"
  metadata:
    namespace: "istio-system"
    name: "istiocontrolplane"
  spec:
    profile: "default"
    components:
      ingressGateways:
        - name: "istio-ingressgateway"
          enabled: true
          k8s:
            service:
              type: "LoadBalancer"
              ports:
                - port: 80
                  targetPort: 8080
                  name: "http"
                - port: 443
                  targetPort: 8443
                  name: "https"
      pilot:
        enabled: true
        k8s:
          resources:
            requests:
              cpu: "500m"
              memory: "2Gi"
    values:
      global:
        proxy:
          resources:
            requests:
              cpu: "100m"
              memory: "128Mi"
            limits:
              cpu: "2000m"
              memory: "1Gi"
      pilot:
        resources:
          requests:
            cpu: "500m"
            memory: "2Gi"

# Istio Gateway
istio-gateway:
  apiVersion: "networking.istio.io/v1beta1"
  kind: "Gateway"
  metadata:
    name: "my-gateway"
    namespace: "production"
  spec:
    selector:
      istio: "ingressgateway"
    servers:
      - port:
          number: 80
          name: "http"
          protocol: "HTTP"
        hosts:
          - "*.myapp.com"

# Virtual Service
virtual-service:
  apiVersion: "networking.istio.io/v1beta1"
  kind: "VirtualService"
  metadata:
    name: "my-app"
    namespace: "production"
  spec:
    hosts:
      - "api.myapp.com"
    gateways:
      - "my-gateway"
    http:
      - match:
          - uri:
              prefix: "/v1"
        route:
          - destination:
              host: "my-app-v1"
        retries:
          attempts: 3
          perTryTimeout: "2s"
      - route:
          - destination:
              host: "my-app-v2"
            weight: 90
          - destination:
              host: "my-app-v1"
            weight: 10
```

### Linkerd Service Mesh
```yaml
# Linkerd Installation
linkerd-install:
  # Install CLI
  curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/install | sh

  # Validate cluster
  linkerd check --pre

  # Install control plane
  linkerd install | kubectl apply -f -

  # Validate installation
  linkerd check

# Linkerd Service Profile
service-profile:
  apiVersion: "linkerd.io/v1alpha2"
  kind: "ServiceProfile"
  metadata:
    name: "my-app.profile"
    namespace: "production"
  spec:
    routes:
      - name: "GET /api/v1/users"
        condition:
          method: "GET"
          pathRegex: "/api/v1/users"
      - name: "POST /api/v1/users"
        condition:
          method: "POST"
          pathRegex: "/api/v1/users"
        isRetryable: false

# Traffic Split for Progressive Delivery
traffic-split:
  apiVersion: "split.smi-spec.io/v1alpha1"
  kind: "TrafficSplit"
  metadata:
    name: "my-app-split"
    namespace: "production"
  spec:
    service: "my-app"
    backends:
      - service: "my-app-v1"
        weight: "90%"
      - service: "my-app-v2"
        weight: "10%"
```

## Security в Cloud Kubernetes

### Pod Security Standards
```yaml
# Pod Security Admission
pod-security-admission:
  apiVersion: "policy/v1beta1"
  kind: "PodSecurityPolicy"
  metadata:
    name: "restricted"
  spec:
    privileged: false
    allowPrivilegeEscalation: false
    requiredDropCapabilities:
      - ALL
    runAsUser:
      rule: "MustRunAsNonRoot"
    seLinux:
      rule: "RunAsAny"
    supplementalGroups:
      rule: "MustRunAs"
      ranges:
        - min: 1
          max: 65535
    fsGroup:
      rule: "MustRunAs"
      ranges:
        - min: 1
          max: 65535
    readOnlyRootFilesystem: true
    volumes:
      - "configMap"
      - "downwardAPI"
      - "emptyDir"
      - "persistentVolumeClaim"
      - "secret"
      - "projected"

# Kyverno Policies
kyverno-policies:
  - name: "require-labels"
    spec:
      validationFailureAction: "enforce"
      rules:
        - name: "check-for-labels"
          match:
            resources:
              kinds:
                - "Pod"
          validate:
            message: "label 'app' is required"
            pattern:
              metadata:
                labels:
                  app: "?*"

  - name: "disallow-latest-tag"
    spec:
      validationFailureAction: "enforce"
      rules:
        - name: "validate-image-tag"
          match:
            resources:
              kinds:
                - "Pod"
          validate:
            message: "Image tag 'latest' is not allowed"
            pattern:
              spec:
                containers:
                  - image: "!*:latest"
```

### Network Policies
```yaml
# Kubernetes Network Policy
network-policy:
  apiVersion: "networking.k8s.io/v1"
  kind: "NetworkPolicy"
  metadata:
    name: "api-allow"
    namespace: "production"
  spec:
    podSelector:
      matchLabels:
        app: "api"
    policyTypes:
      - Ingress
      - Egress
    ingress:
      - from:
          - namespaceSelector:
              matchLabels:
                name: "ingress-nginx"
          - podSelector:
              matchLabels:
                app: "web"
        ports:
          - protocol: "TCP"
            port: 8080
      - from:
          - namespaceSelector:
              matchLabels:
                name: "monitoring"
        ports:
          - protocol: "TCP"
            port: 8080
    egress:
      - to:
          - podSelector:
              matchLabels:
                app: "database"
        ports:
          - protocol: "TCP"
            port: 5432
      - to: []
        ports:
          - protocol: "TCP"
            port: 53
          - protocol: "UDP"
            port: 53
      - to: []
        ports:
          - protocol: "TCP"
            port: 443

# Calico Network Policy
calico-policy:
  apiVersion: "crd.projectcalico.org/v1"
  kind: "NetworkPolicy"
  metadata:
    name: "allow-api-access"
    namespace: "production"
  spec:
    selector: "app == 'api'"
    types:
      - Ingress
      - Egress
    ingress:
      - action: "Allow"
        source:
          selector: "app == 'web'"
        destination:
          ports:
            - 8080
      - action: "Allow"
        source:
          namespaceSelector: "projectcalico.org/name == 'monitoring'"
        destination:
          ports:
            - 8080
    egress:
      - action: "Allow"
        destination:
          selector: "app == 'database'"
          ports:
            - 5432
      - action: "Allow"
        destination:
          nets:
            - "8.8.8.8/32"
          ports:
            - 53
```

### Secrets Management
```yaml
# External Secrets Operator
external-secrets:
  apiVersion: "external-secrets.io/v1beta1"
  kind: "SecretStore"
  metadata:
    name: "aws-secretsmanager"
    namespace: "production"
  spec:
    provider:
      aws:
        service: "SecretsManager"
        region: "us-east-1"
        auth:
          jwt:
            serviceAccountRef:
              name: "external-secrets-sa"

  apiVersion: "external-secrets.io/v1beta1"
  kind: "ExternalSecret"
  metadata:
    name: "db-secret"
    namespace: "production"
  spec:
    refreshInterval: "15s"
    secretStoreRef:
      name: "aws-secretsmanager"
      kind: "SecretStore"
    target:
      name: "db-secret"
      creationPolicy: "Owner"
    data:
      - secretKey: "password"
        remoteRef:
          key: "prod/database/password"
      - secretKey: "username"
        remoteRef:
          key: "prod/database/username"

# Sealed Secrets
sealed-secrets:
  apiVersion: "bitnami.com/v1alpha1"
  kind: "SealedSecret"
  metadata:
    name: "my-secret"
    namespace: "production"
  spec:
    encryptedData:
      password: "AgBy3i4OJSWK+PiTySYZZLpPnaB078=="
      username: "AgBy3i4OJSWK+PiTySYZZLpPnaB078=="
    template:
      metadata:
        labels:
          app: "my-app"
      type: "Opaque"
```

## Cost Optimization

### Cluster Autoscaling
```yaml
# Cluster Autoscaler (AWS)
cluster-autoscaler-aws:
  serviceAccount:
    name: "cluster-autoscaler"
    annotations:
      eks.amazonaws.com/role-arn: "arn:aws:iam::account:role/cluster-autoscaler-role"

  deployment:
    spec:
      template:
        spec:
          containers:
            - name: "cluster-autoscaler"
              image: "k8s.gcr.io/autoscaling/cluster-autoscaler:v1.24.0"
              command:
                - "./cluster-autoscaler"
                - "--v=4"
                - "--stderrthreshold=info"
                - "--cloud-provider=aws"
                - "--skip-nodes-with-local-storage=false"
                - "--expander=least-waste"
                - "--node-group-auto-discovery=asg:tag=k8s.io/cluster-autoscaler/enabled,k8s.io/cluster-autoscaler/eks-prod-cluster"
              resources:
                limits:
                  cpu: "100m"
                  memory: "600Mi"
                requests:
                  cpu: "100m"
                  memory: "600Mi"

# Karpenter (AWS)
karpenter-config:
  apiVersion: "karpenter.sh/v1alpha5"
  kind: "Provisioner"
  metadata:
    name: "default"
  spec:
    requirements:
      - key: "karpenter.sh/capacity-type"
        operator: "In"
        values: ["on-demand", "spot"]
      - key: "node.kubernetes.io/instance-type"
        operator: "In"
        values: ["t3.medium", "t3.large", "t3.xlarge"]
    limits:
      resources:
        cpu: "1000"
        memory: "1000Gi"
    provider:
      subnetSelector:
        karpenter.sh/discovery: "eks-prod-cluster"
      securityGroupSelector:
        karpenter.sh/discovery: "eks-prod-cluster"
      tags:
        karpenter.sh/discovery: "eks-prod-cluster"
    ttlSecondsAfterEmpty: 30
```

### Spot Instances и Preemptibles
```yaml
# AWS EKS with Spot Instances
eks-spot-node-group:
  name: "ng-spot"
  clusterName: "eks-prod-cluster"
  capacityType: "SPOT"
  instanceTypes:
    - "t3.large"
    - "t3.xlarge"
  scalingConfig:
    minSize: 0
    maxSize: 20
    desiredSize: 5

# GCP GKE Preemptible Nodes
gke-preemptible-nodes:
  name: "pool-preemptible"
  config:
    preemptible: true
    machine_type: "n1-standard-2"
    disk_size_gb: 100
  autoscaling:
    enabled: true
    min_node_count: 0
    max_node_count: 10

# Azure AKS Spot Node Pool
aks-spot-pool:
  name: "poolspot"
  mode: "User"
  scaleSetPriority: "Spot"
  spotMaxPrice: -1  # Pay market price
  scaleSetEvictionPolicy: "Delete"
  osType: "Linux"
  osSKU: "Ubuntu"
  vmSize: "Standard_DS2_v2"
  enableAutoScaling: true
  minCount: 0
  maxCount: 10
```

### Resource Optimization
```yaml
# Vertical Pod Autoscaler
vpa-config:
  apiVersion: "autoscaling.k8s.io/v1"
  kind: "VerticalPodAutoscaler"
  metadata:
    name: "my-app-vpa"
    namespace: "production"
  spec:
    targetRef:
      apiVersion: "apps/v1"
      kind: "Deployment"
      name: "my-app"
    updatePolicy:
      updateMode: "Auto"
    resourcePolicy:
      containerPolicies:
        - containerName: "*"
          minAllowed:
            cpu: "100m"
            memory: "128Mi"
          maxAllowed:
            cpu: "2"
            memory: "4Gi"
          controlledResources: ["cpu", "memory"]

# Addon Resizer for system components
addon-resizer:
  apiVersion: "addons.k8s.io/v1alpha1"
  kind: "AddonResizer"
  metadata:
    name: "kube-dns-autoscaler"
    namespace: "kube-system"
  spec:
    resources:
      limits:
        cpu: "100m"
        memory: "200Mi"
      requests:
        cpu: "50m"
        memory: "100Mi"
```

### Cost Monitoring
```yaml
# Kubecost Installation
kubecost-install:
  helm repo add kubecost https://kubecost.github.io/cost-analyzer/
  helm install kubecost kubecost/cost-analyzer \
    --namespace kubecost \
    --create-namespace \
    --set kubecostToken="your-token"

# Cloud Provider Cost Allocation
aws-cost-allocation:
  # Use AWS Cost Allocation Tags
  tags:
    - key: "kubernetes.io/cluster/eks-prod-cluster"
      value: "owned"
    - key: "kubernetes.io/created-for/pvc/namespace"
      value: "production"

# Azure Cost Management
azure-cost-tags:
  tags:
    - key: "kubernetes.io_cluster_aks-prod-cluster"
      value: "owned"
    - key: "kubernetes.io_created-for_pvc_namespace"
      value: "production"
```

## Лучшие практики

- **Выбор managed-сервиса: EKS**, **AKS**, **GKE** — оценивайте по интеграции с экосистемой облака, стоимости, **SLA** и поддержке (например, автоапдейты, `Fargate`/Autopilot).
- **Безопасность:** включайте **Pod Security Standards**/**Admission**; **Network Policies** для изоляции; шифрование секретов и **etcd**; **IRSA**/**Workload Identity** для доступа к облачным сервисам без статических ключей.
- **Надёжность:** мульти-`AZ` нод-группы; **PDB** для критичных подов; автоскейлинг кластера и подов; регулярные бэкапы (Velero, облачные снимки).
- **Стоимость: Spot**/**Preemptible** ноды для **fault-tolerant** нагрузок; права размеров нод и лимиты ресурсов; мониторинг затрат (Kubecost, облачные теги).
- **GitOps и `IaC`:** управление манифестами через **Git** (Flux, Argo CD); инфраструктура кластера через **Terraform**/**Pulumi**; прозрачность изменений и откат.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[aws-services|AWS Services]] — сервисы **AWS**
- [[azure-services|Azure Services]] — сервисы **Azure**
- [[gcp-services|GCP Services]] — сервисы **GCP**
- [[kubernetes-advanced|Kubernetes Advanced]] — **Kubernetes**
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
