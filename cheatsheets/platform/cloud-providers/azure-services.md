---
title: "Azure Services"
description: "Azure предоставляет более 200 сервисов для различных задач - от вычислений и хранения данных до машинного обучения и IoT. Этот документ охватывает основные сервисы Azure, их назначение, use cases и лучшие практики использования в production средах. Документ дополняет [Azure Basic"
tags:
  - platform
  - cloud-providers
  - azure-services
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Azure Services

**Azure** предоставляет более `200` сервисов для различных задач — от вычислений и хранения данных до машинного обучения и **IoT**. Этот документ охватывает основные сервисы **Azure**, их назначение, **use cases** и лучшие практики использования в **production** средах. Документ дополняет [[azure-basics|Azure Basics]] более глубоким погружением в конкретные сервисы.

## Полезные ссылки
- [Azure Services Documentation](https://docs.microsoft.com/azure/)
- [Azure Architecture Center](https://docs.microsoft.com/azure/architecture/)
- [Azure Pricing Calculator](https://azure.microsoft.com/pricing/calculator/)
- [Azure Well-Architected Framework](https://docs.microsoft.com/azure/architecture/framework/)
- [Azure CLI Documentation](https://docs.microsoft.com/cli/azure/)


### См. также
- [[kubernetes-cloud|Kubernetes в облаке]]
- [[aws-iam|AWS IAM (Identity and Access Management)]]
## Содержание

- [Compute Services](#compute-services)
  - [Azure Virtual Machines](#azure-virtual-machines)
  - [Azure Kubernetes Service (AKS)](#azure-kubernetes-service-aks)
  - [Azure Container Instances (ACI)](#azure-container-instances-aci)
  - [Azure Functions](#azure-functions)
- [Storage Services](#storage-services)
  - [Azure Blob Storage](#azure-blob-storage)
  - [Azure Files](#azure-files)
- [Database Services](#database-services)
  - [Azure SQL Database](#azure-sql-database)
  - [Azure Cosmos DB](#azure-cosmos-db)
- [Networking & Content Delivery](#networking-content-delivery)
  - [Azure Front Door](#azure-front-door)
  - [Azure Application Gateway](#azure-application-gateway)
  - [Azure Traffic Manager](#azure-traffic-manager)
- [Security Services](#security-services)
  - [Azure Key Vault](#azure-key-vault)
  - [Azure Active Directory](#azure-active-directory)
- [Analytics & Big Data](#analytics-big-data)
  - [Azure Synapse Analytics](#azure-synapse-analytics)
  - [Azure Data Factory](#azure-data-factory)
- [Machine Learning & AI](#machine-learning-ai)
  - [Azure Machine Learning](#azure-machine-learning)
  - [Azure Cognitive Services](#azure-cognitive-services)
- [Developer Tools](#developer-tools)
  - [Azure DevOps](#azure-devops)
- [Management & Governance](#management-governance)
  - [Azure Policy](#azure-policy)
  - [Azure Blueprints](#azure-blueprints)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Compute Services

### Azure Virtual Machines

**Что это:** Виртуальные серверы в облаке **Azure**.

**VM `Series` и их назначение:**

Пример серий `VM` **Azure** и назначения (YAML).
```yaml
# Обзор серий VM Azure — назначение и соотношение CPU/память (general-purpose, compute-optimized и др.)
vm-series:
  general-purpose:
    series: "D, Dv3, Dv4"
    use-case: "Web servers, small databases, development"
    cpu-memory: "Balanced ratio"
    storage: "Premium SSD support"

  compute-optimized:
    series: "F, Fs, Fsv2"
    use-case: "Batch processing, web servers, analytics"
    cpu-memory: "High CPU-to-memory ratio"
    features: "Up to 72 vCPUs"

  memory-optimized:
    series: "E, Ev3, Ev4, M"
    use-case: "Relational databases, in-memory analytics"
    cpu-memory: "High memory-to-CPU ratio"
    storage: "Up to 3.8 TB RAM (M-series)"

  storage-optimized:
    series: "L, Ls"
    use-case: "Big data, SQL/NoSQL databases, data warehousing"
    storage: "Up to 32 TB local SSD"
    features: "High disk throughput"

  gpu-enabled:
    series: "N, NC, ND, NV"
    use-case: "AI/ML training, graphics rendering, visualization"
    gpu: "NVIDIA GPUs"
    features: "InfiniBand interconnect (NC/ND)"

  high-performance:
    series: "H, HB, HC"
    use-case: "HPC workloads, molecular modeling"
    interconnect: "InfiniBand"
    features: "Low-latency networking"
```

**Availability `Sets` и `Availability Zones`:**
```yaml
# Availability Set
availability-set:
  name: "as-web-prod"
  platformFaultDomainCount: 3
  platformUpdateDomainCount: 5
  proximityPlacementGroup:
    id: "/subscriptions/.../resourceGroups/rg-prod/providers/Microsoft.Compute/proximityPlacementGroups/ppg-web"

# Availability Zone Configuration
availability-zone-config:
  zones: ["1", "2", "3"]
  strategy:
    type: "AvailabilityZone"
    zones: ["1", "2", "3"]

# VM with Availability Zone
vm-with-az:
  name: "vm-web-01"
  location: "East US 2"
  zones: ["1"]
  sku: "Standard_DS1_v2"
  osProfile:
    computerName: "vm-web-01"
    adminUsername: "azureuser"
```

**Azure `Automanage`:**
```yaml
# Azure Automanage Configuration
automanage-config:
  name: "prod-automanage"
  location: "East US 2"
  properties:
    configurationProfile: "/providers/Microsoft.Automanage/bestPractices/AzureBestPracticesProduction"

# Automanage Assignment
automanage-assignment:
  name: "vm-automanage"
  type: "Microsoft.Compute/virtualMachines/providers/automanageAssignments"
  properties:
    configurationProfile: "/providers/Microsoft.Automanage/bestPractices/AzureBestPracticesProduction"
```

### Azure Kubernetes Service (AKS)

**Что это:** Управляемый **Kubernetes** сервис.

**AKS `Cluster configuration`:**
```yaml
# AKS Cluster
aks-cluster:
  name: "aks-prod"
  location: "East US 2"
  resourceGroup: "rg-aks-prod"
  kubernetesVersion: "1.24.6"
  dnsPrefix: "aks-prod"
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
      maxCount: 10
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

**AKS `Node Pools`:**
```yaml
# Additional Node Pool
additional-node-pool:
  name: "userpool"
  mode: "User"
  osType: "Linux"
  osSKU: "Ubuntu"
  count: 5
  vmSize: "Standard_DS3_v2"
  osDiskSizeGB: 256
  osDiskType: "Managed"
  enableAutoScaling: true
  minCount: 3
  maxCount: 20
  availabilityZones: ["1", "2", "3"]
  nodeLabels:
    environment: "production"
    workload: "application"
  nodeTaints:
    - key: "workload"
      value: "application"
      effect: "NoSchedule"
  upgradeSettings:
    maxSurge: "10%"
```

**AKS `Integration` с `Azure Services`:**
```yaml
# AKS with Azure Monitor
aks-monitoring:
  addonProfiles:
    omsAgent:
      enabled: true
      config:
        logAnalyticsWorkspaceResourceID: "/subscriptions/.../resourceGroups/rg-monitoring/providers/Microsoft.OperationalInsights/workspaces/law-prod"

# AKS with Azure Key Vault
aks-keyvault:
  addonProfiles:
    azureKeyvaultSecretsProvider:
      enabled: true
      config:
        enableSecretRotation: "true"
        rotationPollInterval: "2m"

# AKS with Azure Policy
aks-policy:
  addonProfiles:
    azurePolicy:
      enabled: true
```

### Azure Container Instances (ACI)

**Что это: Serverless** контейнеры без управления виртуальными машинами.

**ACI `Container Groups`:**
```yaml
# Single Container
single-container:
  name: "aci-hello-world"
  location: "East US"
  properties:
    containers:
      - name: "hello-world"
        properties:
          image: "microsoft/aci-helloworld"
          ports:
            - port: 80
          resources:
            requests:
              cpu: 1.0
              memoryInGB: 1.5
    osType: "Linux"
    restartPolicy: "Always"
    ipAddress:
      type: "Public"
      ports:
        - protocol: "tcp"
          port: 80

# Multi-container Group
multi-container-group:
  name: "aci-app-with-sidecar"
  location: "East US"
  properties:
    containers:
      - name: "web-app"
        properties:
          image: "myapp:latest"
          ports:
            - port: 80
          resources:
            requests:
              cpu: 1.0
              memoryInGB: 1.5
          environmentVariables:
            - name: "DATABASE_URL"
              secureValue: "Server=tcp:mydb.database.windows.net;Database=myDB;..."

      - name: "log-shipper"
        properties:
          image: "fluent/fluent-bit:latest"
          resources:
            requests:
              cpu: 0.5
              memoryInGB: 0.5
          volumeMounts:
            - name: "logs"
              mountPath: "/var/log"
    osType: "Linux"
    restartPolicy: "Always"
    volumes:
      - name: "logs"
        emptyDir: {}
    ipAddress:
      type: "Public"
      ports:
        - protocol: "tcp"
          port: 80
```

### Azure Functions

**Что это: Serverless** функции для **event-driven** приложений.

**Function `App Configuration`:**
```yaml
# Function App
function-app:
  name: "func-prod-eastus"
  location: "East US"
  kind: "functionapp"
  properties:
    httpsOnly: true
    serverFarmId: "/subscriptions/.../serverFarms/asp-prod"
    siteConfig:
      linuxFxVersion: "NODE|16"
      use32BitWorkerProcess: false
      alwaysOn: true
      functionAppScaleLimit: 200
      minimumElasticInstanceCount: 1
      cors:
        allowedOrigins:
          - "https://myapp.azurewebsites.net"
      ipSecurityRestrictions:
        - ipAddress: "10.0.0.0/8"
          action: "Allow"
          priority: 1
        - ipAddress: "192.168.0.0/16"
          action: "Allow"
          priority: 2
    identity:
      type: "SystemAssigned"

# Function Triggers
function-triggers:
  http-trigger:
    name: "HttpTrigger"
    type: "httpTrigger"
    direction: "in"
    authLevel: "function"
    methods: ["get", "post"]

  timer-trigger:
    name: "TimerTrigger"
    type: "timerTrigger"
    direction: "in"
    schedule: "0 */5 * * * *"  # Every 5 minutes

  queue-trigger:
    name: "QueueTrigger"
    type: "queueTrigger"
    direction: "in"
    queueName: "myqueue"
    connection: "AzureWebJobsStorage"

  event-hub-trigger:
    name: "EventHubTrigger"
    type: "eventHubTrigger"
    direction: "in"
    eventHubName: "myeventhub"
    consumerGroup: "$Default"
    connection: "EventHubConnection"
```

**Durable `Functions`:**
```javascript
// Durable Function Orchestrator
const df = require("durable-functions");

module.exports = df.orchestrator(function* (context) {
    const outputs = [];

    // Call activity functions in sequence
    outputs.push(yield context.df.callActivity("Activity1", "input1"));
    outputs.push(yield context.df.callActivity("Activity2", "input2"));

    // Call activity functions in parallel
    const parallelTasks = [
        context.df.callActivity("Activity3", "input3"),
        context.df.callActivity("Activity4", "input4")
    ];
    const parallelResults = yield context.df.Task.all(parallelTasks);
    outputs.push(...parallelResults);

    return outputs;
});

// Durable Function Activity
module.exports = async function (context) {
    // Activity function logic
    const result = await processData(context.bindings.input);
    return result;
};
```

## Storage Services

### Azure Blob Storage

**Что это:** Объектное хранилище для неструктурированных данных.

**Storage `Account Tiers`:**
```yaml
# Storage Account Configuration
storage-account:
  name: "stprodstorage"
  kind: "StorageV2"
  sku:
    name: "Standard_RAGRS"  # Read-access geo-redundant
  location: "East US 2"
  allowBlobPublicAccess: false
  minimumTlsVersion: "TLS1_2"
  supportsHttpsTrafficOnly: true
  isHnsEnabled: true  # Hierarchical namespace for Data Lake
  networkAcls:
    defaultAction: "Deny"
    virtualNetworkRules:
      - virtualNetworkResourceId: "/subscriptions/.../resourceGroups/rg-prod/providers/Microsoft.Network/virtualNetworks/vnet-prod"
        action: "Allow"
    ipRules:
      - value: "203.0.113.1"
        action: "Allow"

# Blob Service Properties
blob-service-properties:
  cors:
    corsRules:
      - allowedOrigins: ["https://myapp.azurewebsites.net"]
        allowedMethods: ["GET", "HEAD", "POST", "PUT"]
        allowedHeaders: ["*"]
        exposedHeaders: ["*"]
        maxAgeInSeconds: 3600
  deleteRetentionPolicy:
    enabled: true
    days: 7
  staticWebsite:
    enabled: true
    indexDocument: "index.html"
    errorDocument404Path: "404.html"
```

**Lifecycle `Management`:**
```yaml
# Blob Lifecycle Management
lifecycle-management:
  rules:
    - name: "move-to-cool"
      enabled: true
      type: "Lifecycle"
      definition:
        filters:
          blobTypes: ["blockBlob"]
          prefixMatch: ["logs/"]
        actions:
          baseBlob:
            tierToCool:
              daysAfterModificationGreaterThan: 30
          snapshot:
            delete:
              daysAfterCreationGreaterThan: 90

    - name: "move-to-archive"
      enabled: true
      type: "Lifecycle"
      definition:
        filters:
          blobTypes: ["blockBlob"]
          prefixMatch: ["backups/"]
        actions:
          baseBlob:
            tierToArchive:
              daysAfterModificationGreaterThan: 90
          snapshot:
            tierToArchive:
              daysAfterCreationGreaterThan: 90

    - name: "delete-old-logs"
      enabled: true
      type: "Lifecycle"
      definition:
        filters:
          blobTypes: ["blockBlob"]
          prefixMatch: ["temp/"]
        actions:
          baseBlob:
            delete:
              daysAfterModificationGreaterThan: 7
```

**Azure Data Lake Storage:**
```yaml
# ADLS Gen2 Filesystem
filesystem:
  name: "data-lake-fs"
  properties:
    lastModified: "2023-01-01T00:00:00Z"
    etag: "\"0x8DAB123456789AB\""
    leaseStatus: "unlocked"
    leaseState: "available"

# Directory Structure
directory-structure:
  - name: "raw-data/"
    properties:
      lastModified: "2023-01-01T00:00:00Z"
      permissions: "rwxrwxrwx"
      acl: "user::rwx,group::r-x,other::r--"

  - name: "processed-data/"
    properties:
      lastModified: "2023-01-01T00:00:00Z"

  - name: "analytics-data/"
    properties:
      lastModified: "2023-01-01T00:00:00Z"
```

### Azure Files

**Что это:** Управляемые файловые **shares** в облаке.

**Azure `File Share`:**
```yaml
# File Share
file-share:
  name: "fileshare-prod"
  properties:
    shareQuota: 5120  # 5 TB
    enabledProtocols: "SMB"
    rootSquash: "NoRootSquash"

# SMB Configuration
smb-config:
  properties:
    cors:
      corsRules:
        - allowedOrigins: ["*"]
          allowedMethods: ["GET", "HEAD", "POST", "PUT"]
          allowedHeaders: ["*"]
          exposedHeaders: ["*"]
          maxAgeInSeconds: 3600
    fileServiceSmbSettings:
      smbMultichannel:
        enabled: true
      smbVersions: ["SMB2.1", "SMB3.0", "SMB3.1.1"]
      smbAuthenticationMethods: ["NTLMv2", "Kerberos"]
      smbKerberosTicketEncryption: ["RC4-HMAC", "AES-256"]
      smbChannelEncryption: ["AES-128-CCM", "AES-128-GCM", "AES-256-GCM"]

# NFS Configuration (Premium tier)
nfs-config:
  properties:
    enabledProtocols: "NFS"
    rootSquash: "NoRootSquash"
    export:
      spec: "%rw"
      rootSquash: "no"
      anonymousUid: "0"
      anonymousGid: "0"
```

**File `Sync`:**
```yaml
# Azure File Sync
storage-sync-service:
  name: "filesync-prod"
  location: "East US 2"
  properties:
    incomingTrafficPolicy: "AllowAllTraffic"

# Sync Group
sync-group:
  name: "sync-group-01"
  properties:
    cloudEndpoint:
      storageAccountResourceId: "/subscriptions/.../resourceGroups/rg-storage/providers/Microsoft.Storage/storageAccounts/stprodstorage"
      azureFileShareName: "fileshare-prod"
      storageAccountTenantId: "12345678-1234-1234-1234-123456789012"

# Server Endpoint
server-endpoint:
  name: "server-endpoint-01"
  properties:
    serverLocalPath: "D:\\SyncShare"
    cloudTiering: "on"
    volumeFreeSpacePercent: 20
    tierFilesOlderThanDays: 30
    friendlyName: "Production File Share"
```

## Database Services

### Azure SQL Database

**Что это:** Управляемая реляционная база данных.

**SQL `Database Configuration`:**
```yaml
# SQL Server
sql-server:
  name: "sql-prod-server"
  location: "East US 2"
  properties:
    administratorLogin: "sqladmin"
    administratorLoginPassword: "{{secret}}"
    version: "12.0"
    publicNetworkAccess: "Disabled"
    minimalTlsVersion: "1.2"

# SQL Database
sql-database:
  name: "sqldb-prod"
  location: "East US 2"
  sku:
    name: "GP_Gen5"
    tier: "GeneralPurpose"
    capacity: 2
    family: "Gen5"
  properties:
    collation: "SQL_Latin1_General_CP1_CI_AS"
    maxSizeBytes: 34359738368  # 32 GB
    zoneRedundant: true
    readScale: "Disabled"
    highAvailabilityReplicaCount: 1
    backupStorageRedundancy: "Geo"
    isLedgerOn: false
    useFreeLimit: false
    freeLimitExhaustionBehavior: "AutoPause"

# Database Firewall Rules
firewall-rules:
  - name: "AllowAzureServices"
    startIpAddress: "0.0.0.0"
    endIpAddress: "0.0.0.0"
  - name: "AllowApplicationSubnet"
    startIpAddress: "10.0.1.0"
    endIpAddress: "10.0.1.255"
```

**Read `Replicas`:**
```yaml
# Read Replica
read-replica:
  name: "sqldb-prod-replica"
  location: "West US 2"
  sku:
    name: "HS_Gen5"
    tier: "Hyperscale"
    capacity: 2
    family: "Gen5"
  properties:
    createMode: "Secondary"
    sourceDatabaseId: "/subscriptions/.../resourceGroups/rg-db/providers/Microsoft.Sql/servers/sql-prod-server/databases/sqldb-prod"
    zoneRedundant: false
    readScale: "Enabled"
    highAvailabilityReplicaCount: 0
```

**Azure `SQL Managed Instance`:**
```yaml
# SQL Managed Instance
sql-managed-instance:
  name: "sqlmi-prod"
  location: "East US 2"
  sku:
    name: "GP_Gen5"
    tier: "GeneralPurpose"
    capacity: 8
    family: "Gen5"
  properties:
    administratorLogin: "sqladmin"
    administratorLoginPassword: "{{secret}}"
    subnetId: "/subscriptions/.../resourceGroups/rg-db/providers/Microsoft.Network/virtualNetworks/vnet-db/subnets/subnet-sqlmi"
    timezoneId: "UTC"
    collation: "SQL_Latin1_General_CP1_CI_AS"
    proxyOverride: "Default"
    publicDataEndpointEnabled: false
    storageSizeInGB: 1024
    vCores: 8
    licenseType: "LicenseIncluded"
    zoneRedundant: true
    maintenanceConfigurationId: "/subscriptions/.../providers/Microsoft.Maintenance/publicMaintenanceConfigurations/SQL_Default"
```

### Azure Cosmos `DB`

**Что это:** Глобально распределенная **NoSQL** база данных.

**Cosmos `DB Account`:**
```yaml
# Cosmos DB Account
cosmos-account:
  name: "cosmos-prod"
  location: "East US 2"
  kind: "GlobalDocumentDB"
  properties:
    consistencyPolicy:
      defaultConsistencyLevel: "Session"
      maxIntervalInSeconds: 5
      maxStalenessPrefix: 100
    locations:
      - locationName: "East US 2"
        failoverPriority: 0
        isZoneRedundant: true
      - locationName: "West US 2"
        failoverPriority: 1
        isZoneRedundant: true
    databaseAccountOfferType: "Standard"
    enableAutomaticFailover: true
    enableMultipleWriteLocations: false
    enableAnalyticalStorage: true
    analyticalStorageConfiguration:
      schemaType: "FullFidelity"
    backupPolicy:
      type: "Continuous"
    cors:
      - allowedOrigins: ["https://myapp.azurewebsites.net"]
        allowedMethods: ["GET", "POST", "PUT", "DELETE"]
        allowedHeaders: ["*"]
        exposedHeaders: ["*"]
        maxAgeInSeconds: 3600
```

**Cosmos `DB Containers`:**
```yaml
# SQL API Database
sql-database:
  id: "ecommerce-db"
  containers:
    - id: "products"
      partitionKey:
        paths: ["/category"]
        kind: "Hash"
      indexingPolicy:
        indexingMode: "consistent"
        includedPaths:
          - path: "/*"
        excludedPaths:
          - path: "/metadata/*"
      defaultTtl: -1
      uniqueKeyPolicy:
        uniqueKeys:
          - paths: ["/sku"]

    - id: "orders"
      partitionKey:
        paths: ["/customerId"]
        kind: "Hash"
      indexingPolicy:
        indexingMode: "consistent"
        includedPaths:
          - path: "/*"
        excludedPaths:
          - path: "/logs/*"
      defaultTtl: 2592000  # 30 days
```

**Cosmos `DB Change Feed`:**
```javascript
// Change Feed Processor
const { ChangeFeedProcessor, ChangeFeedIterator } = require("@azure/cosmos");

async function processChanges() {
    const changeFeedIterator = container.items.getChangeFeedIterator({
        startFromBeginning: true
    });

    while (changeFeedIterator.hasMoreResults) {
        const { result: changes } = await changeFeedIterator.readNext();

        for (const change of changes) {
            console.log(`Change detected: ${change.id}`);

            // Process change
            if (change.operationType === 'create') {
                await processNewDocument(change);
            } else if (change.operationType === 'update') {
                await processUpdatedDocument(change);
            }
        }
    }
}

// Azure Function trigger for Change Feed
module.exports = async function (context, documents) {
    for (const doc of documents) {
        context.log(`Processing document: ${doc.id}`);

        // Business logic here
        await processDocument(doc);
    }
};
```

## Networking & Content Delivery

### Azure Front Door

**Что это:** Глобальный **CDN** и **load balancer**.

**Front `Door Configuration`:**
```yaml
# Front Door Profile
front-door-profile:
  name: "afd-prod"
  location: "global"
  sku:
    name: "Premium_AzureFrontDoor"

# Front Door Endpoint
front-door-endpoint:
  name: "afd-endpoint"
  properties:
    enabledState: "Enabled"

# Front Door Origin Group
origin-group:
  name: "origin-group-web"
  properties:
    loadBalancingSettings:
      sampleSize: 4
      successfulSamplesRequired: 3
      additionalLatencyMilliseconds: 50
    healthProbeSettings:
      path: "/health"
      protocol: "Https"
      intervalInSeconds: 30
      resourceType: ""
      expectedStatusCodeRanges:
        - from: 200
          to: 299

# Front Door Origins
origins:
  - name: "origin-web-east"
    properties:
      hostName: "web-app-east.azurewebsites.net"
      httpPort: 80
      httpsPort: 443
      originHostHeader: "web-app-east.azurewebsites.net"
      priority: 1
      weight: 1000
      enabledState: "Enabled"

  - name: "origin-web-west"
    properties:
      hostName: "web-app-west.azurewebsites.net"
      httpPort: 80
      httpsPort: 443
      originHostHeader: "web-app-west.azurewebsites.net"
      priority: 1
      weight: 1000
      enabledState: "Enabled"
```

**Front `Door Rules`:**
```yaml
# Front Door Rule Set
rule-set:
  name: "rule-set-prod"

# Front Door Rules
rules:
  - name: "redirect-http-to-https"
    properties:
      order: 1
      conditions:
        - name: "RequestScheme"
          parameters:
            operator: "Equal"
            matchValues: ["HTTP"]
      actions:
        - name: "UrlRedirect"
          parameters:
            redirectType: "Moved"
            redirectProtocol: "Https"
            customHostname: ""
            customPath: ""
            customQueryString: ""
            customFragment: ""

  - name: "route-api-requests"
    properties:
      order: 2
      conditions:
        - name: "RequestURL"
          parameters:
            operator: "BeginsWith"
            matchValues: ["/api/"]
      actions:
        - name: "RouteConfigurationOverride"
          parameters:
            originGroup:
              id: "/subscriptions/.../resourceGroups/rg-afd/providers/Microsoft.Cdn/profiles/afd-prod/originGroups/origin-group-api"
            forwardingProtocol: "HttpsOnly"
            cacheConfiguration:
              queryStringCachingBehavior: "IgnoreQueryString"

  - name: "add-security-headers"
    properties:
      order: 3
      actions:
        - name: "ModifyResponseHeader"
          parameters:
            headerAction: "Overwrite"
            headerName: "X-Frame-Options"
            value: "DENY"
        - name: "ModifyResponseHeader"
          parameters:
            headerAction: "Overwrite"
            headerName: "X-Content-Type-Options"
            value: "nosniff"
```

### Azure Application Gateway

**Что это: Web traffic load balancer** с **SSL termination**.

**Application `Gateway v2`:**
```yaml
# Application Gateway
application-gateway:
  name: "agw-prod"
  location: "East US 2"
  properties:
    sku:
      name: "Standard_v2"
      tier: "Standard_v2"
      capacity: 2
    gatewayIPConfigurations:
      - name: "appGatewayIpConfig"
        properties:
          subnet:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/virtualNetworks/vnet-agw/subnets/subnet-agw"
    frontendIPConfigurations:
      - name: "appGatewayFrontendIP"
        properties:
          privateIPAddress: "10.0.1.100"
          privateIPAllocationMethod: "Static"
          subnet:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/virtualNetworks/vnet-agw/subnets/subnet-agw"
    frontendPorts:
      - name: "port_80"
        properties:
          port: 80
      - name: "port_443"
        properties:
          port: 443
    sslCertificates:
      - name: "ssl-cert"
        properties:
          keyVaultSecretId: "https://mykeyvault.vault.azure.net/secrets/ssl-cert"
    trustedRootCertificates:
      - name: "backend-cert"
        properties:
          data: "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    backendAddressPools:
      - name: "appGatewayBackendPool"
        properties:
          backendAddresses:
            - ipAddress: "10.0.2.10"
            - ipAddress: "10.0.2.11"
            - ipAddress: "10.0.2.12"
    backendHttpSettingsCollection:
      - name: "appGatewayBackendHttpSettings"
        properties:
          port: 80
          protocol: "Http"
          cookieBasedAffinity: "Disabled"
          pickHostNameFromBackendAddress: false
          hostName: "backend.internal"
          requestTimeout: 30
          probe:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/probes/healthProbe"
    httpListeners:
      - name: "appGatewayHttpListener"
        properties:
          frontendIPConfiguration:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/frontendIPConfigurations/appGatewayFrontendIP"
          frontendPort:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/frontendPorts/port_80"
          protocol: "Http"
    urlPathMaps:
      - name: "urlPathMap"
        properties:
          defaultBackendAddressPool:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/backendAddressPools/appGatewayBackendPool"
          defaultBackendHttpSettings:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/backendHttpSettingsCollection/appGatewayBackendHttpSettings"
          pathRules:
            - name: "apiPathRule"
              properties:
                paths: ["/api/*"]
                backendAddressPool:
                  id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/backendAddressPools/apiBackendPool"
                backendHttpSettings:
                  id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/backendHttpSettingsCollection/appGatewayBackendHttpSettings"
    requestRoutingRules:
      - name: "rule1"
        properties:
          ruleType: "PathBasedRouting"
          httpListener:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/httpListeners/appGatewayHttpListener"
          urlPathMap:
            id: "/subscriptions/.../resourceGroups/rg-agw/providers/Microsoft.Network/applicationGateways/agw-prod/urlPathMaps/urlPathMap"
    probes:
      - name: "healthProbe"
        properties:
          protocol: "Http"
          host: "backend.internal"
          path: "/health"
          interval: 30
          timeout: 30
          unhealthyThreshold: 3
          pickHostNameFromBackendHttpSettings: true
    autoscaleConfiguration:
      minCapacity: 2
      maxCapacity: 10
```

### Azure Traffic Manager

**Что это: DNS-based traffic load balancer**.

**Traffic `Manager Profile`:**
```yaml
# Traffic Manager Profile
traffic-manager-profile:
  name: "tm-prod"
  profileStatus: "Enabled"
  trafficRoutingMethod: "Weighted"
  dnsConfig:
    relativeName: "myapp"
    ttl: 30
  monitorConfig:
    protocol: "HTTPS"
    port: 443
    path: "/health"
    intervalInSeconds: 30
    timeoutInSeconds: 10
    toleratedNumberOfFailures: 3
  endpoints:
    - name: "endpoint-eastus"
      type: "Microsoft.Network/trafficManagerProfiles/azureEndpoints"
      properties:
        targetResourceId: "/subscriptions/.../resourceGroups/rg-east/providers/Microsoft.Cdn/profiles/afd-prod/endpoints/afd-endpoint"
        weight: 50
        endpointStatus: "Enabled"
        geoMapping: ["GEO-NA"]

    - name: "endpoint-westeurope"
      type: "Microsoft.Network/trafficManagerProfiles/externalEndpoints"
      properties:
        target: "myapp-eu.contoso.com"
        weight: 30
        endpointStatus: "Enabled"
        geoMapping: ["GEO-EU"]

    - name: "endpoint-asia"
      type: "Microsoft.Network/trafficManagerProfiles/nestedEndpoints"
      properties:
        targetResourceId: "/subscriptions/.../resourceGroups/rg-asia/providers/Microsoft.Network/trafficManagerProfiles/tm-asia"
        weight: 20
        endpointStatus: "Enabled"
        geoMapping: ["GEO-AS"]
        minChildEndpoints: 2
```

## Security Services

### Azure Key Vault

**Что это:** Управление секретами, ключами и сертификатами.

**Key `Vault Configuration`:**
```yaml
# Key Vault
key-vault:
  name: "kv-prod-secrets"
  location: "East US 2"
  properties:
    sku:
      family: "A"
      name: "standard"
    tenantId: "12345678-1234-1234-1234-123456789012"
    enabledForDeployment: false
    enabledForTemplateDeployment: true
    enabledForDiskEncryption: true
    enableSoftDelete: true
    softDeleteRetentionInDays: 90
    enableRbacAuthorization: true
    networkAcls:
      defaultAction: "Deny"
      bypass: "AzureServices"
      virtualNetworkRules:
        - id: "/subscriptions/.../resourceGroups/rg-vnet/providers/Microsoft.Network/virtualNetworks/vnet-prod/subnets/subnet-app"
      ipRules:
        - value: "203.0.113.1"
    accessPolicies: []

# Key Vault Secrets
secrets:
  - name: "db-connection-string"
    properties:
      value: "Server=tcp:mydb.database.windows.net,1433;Database=myDB;..."
      contentType: "text/plain"
      attributes:
        enabled: true
        exp: 1704067200  # 2024-01-01

  - name: "api-key"
    properties:
      value: "sk-1234567890abcdef"
      contentType: "text/plain"

# Key Vault Keys
keys:
  - name: "encryption-key"
    properties:
      kty: "RSA"
      keySize: 2048
      keyOps: ["encrypt", "decrypt", "sign", "verify"]
      attributes:
        enabled: true

# Key Vault Certificates
certificates:
  - name: "ssl-cert"
    properties:
      subject: "CN=myapp.com"
      validityInMonths: 12
      keyProperties:
        exportable: true
        keySize: 2048
        keyType: "RSA"
        reuseKey: false
      issuerParameters:
        name: "Self"
      x509CertificateProperties:
        subjectAlternativeNames:
          dnsNames: ["myapp.com", "www.myapp.com"]
        keyUsage: ["digitalSignature", "keyEncipherment"]
        extendedKeyUsage: ["serverAuth"]
```

### Azure Active Directory

**Что это: Identity** и **access management** сервис.

**Conditional `Access Policies`:**
```yaml
# Conditional Access Policy
conditional-access-policy:
  displayName: "Require MFA for admins"
  state: "enabled"
  conditions:
    users:
      includeUsers:
        - "admin-users-group"
      excludeUsers:
        - "emergency-access-accounts"
    applications:
      includeApplications:
        - "All"
    locations:
      includeLocations:
        - "All"
      excludeLocations:
        - "trusted-locations"
    clientAppTypes:
      - "browser"
      - "mobileAppsAndDesktopClients"
    signInRiskLevels:
      - "high"
      - "medium"
    userRiskLevels:
      - "high"
      - "medium"
  grantControls:
    operator: "OR"
    builtInControls:
      - "mfa"
      - "compliantDevice"
      - "domainJoinedDevice"
  sessionControls:
    applicationEnforcedRestrictions:
      isEnabled: true
    cloudAppSecurity:
      isEnabled: true
      cloudAppSecurityType: "monitorOnly"
    signInFrequency:
      isEnabled: true
      type: "hours"
      value: 4
    persistentBrowser:
      isEnabled: true
      mode: "never"
```

**Azure `AD Connect`:**
```yaml
# Azure AD Connect Configuration
ad-connect-config:
  sync:
    source: "Windows Server AD"
    target: "Azure AD"
    features:
      - passwordHashSync: true
      - passwordWriteback: true
      - groupWriteback: true
      - deviceWriteback: true
      - directoryExtensionAttributeSync: true

  authentication:
    methods:
      - pta: "Pass-through Authentication"
      - adfs: "Active Directory Federation Services"
      - pims: "Password Hash Synchronization + Seamless SSO"

  filtering:
    domainFiltering:
      - include: ["contoso.com"]
      - exclude: ["test.contoso.com"]
    organizationalUnitFiltering:
      - include: ["OU=Users,DC=contoso,DC=com"]
    groupFiltering:
      - include: ["SyncUsers"]
```

## Analytics & Big Data

### Azure Synapse Analytics

**Что это: Analytics service** для **big data** и **data warehousing**.

**Synapse `Workspace`:**
```yaml
# Synapse Workspace
synapse-workspace:
  name: "synapse-prod"
  location: "East US 2"
  properties:
    defaultDataLakeStorage:
      accountUrl: "https://stprodstorage.dfs.core.windows.net"
      filesystem: "data"
    sqlAdministratorLogin: "sqladmin"
    sqlAdministratorLoginPassword: "{{secret}}"
    managedVirtualNetwork: "default"
    publicNetworkAccess: "Disabled"
    managedResourceGroupName: "rg-synapse-managed"
    encryption:
      cmk:
        key:
          name: "synapse-key"
          keyVaultUrl: "https://kv-prod-keys.vault.azure.net/"
    workspaceRepositoryConfiguration:
      type: "FactoryGitHubConfiguration"
      hostName: ""
      accountName: "myorg"
      projectName: "data-analytics"
      repositoryName: "synapse-repo"
      collaborationBranch: "main"
      rootFolder: "/"
```

**Dedicated `SQL Pool`:**
```sql
-- Create Dedicated SQL Pool
CREATE TABLE Sales.FactSales
(
    SalesKey int NOT NULL,
    DateKey datetime NOT NULL,
    CustomerKey int NOT NULL,
    ProductKey int NOT NULL,
    SalesAmount money NOT NULL,
    TaxAmount money NOT NULL
)
WITH
(
    DISTRIBUTION = HASH(ProductKey),
    CLUSTERED COLUMNSTORE INDEX
);

-- Load data from Data Lake
COPY INTO Sales.FactSales
FROM 'https://stprodstorage.dfs.core.windows.net/data/sales/*.parquet'
WITH
(
    FILE_TYPE = 'PARQUET',
    CREDENTIAL = (IDENTITY = 'Managed Identity')
);
```

**Serverless `SQL Pool`:**
```sql
-- Query Parquet files directly
SELECT
    YEAR(sale_date) as sale_year,
    MONTH(sale_date) as sale_month,
    product_category,
    SUM(sales_amount) as total_sales,
    COUNT(*) as transaction_count
FROM
    OPENROWSET(
        BULK 'https://stprodstorage.dfs.core.windows.net/data/sales/year=*/month=*/*.parquet',
        FORMAT = 'PARQUET'
    ) AS [sales]
GROUP BY
    YEAR(sale_date),
    MONTH(sale_date),
    product_category
ORDER BY
    sale_year DESC,
    sale_month DESC,
    total_sales DESC;
```

### Azure Data Factory

**Что это: Data integration** и **ETL** сервис.

**Data `Factory Pipeline`:**
```json
{
  "name": "ETL_Pipeline",
  "properties": {
    "activities": [
      {
        "name": "CopyFromBlobToSQL",
        "type": "Copy",
        "dependsOn": [],
        "policy": {
          "timeout": "7.00:00:00",
          "retry": 0,
          "retryIntervalInSeconds": 30,
          "secureOutput": false,
          "secureInput": false
        },
        "typeProperties": {
          "source": {
            "type": "DelimitedTextSource",
            "storeSettings": {
              "type": "AzureBlobStorageReadSettings",
              "recursive": true
            },
            "formatSettings": {
              "type": "DelimitedTextReadSettings"
            }
          },
          "sink": {
            "type": "SqlSink",
            "writeBehavior": "insert",
            "sqlWriterUseTableLock": false
          },
          "enableStaging": false,
          "dataIntegrationUnits": 4
        },
        "inputs": [
          {
            "referenceName": "SourceDataset",
            "type": "DatasetReference"
          }
        ],
        "outputs": [
          {
            "referenceName": "SinkDataset",
            "type": "DatasetReference"
          }
        ]
      }
    ]
  }
}
```

## Machine Learning & `AI`

### Azure Machine Learning

**Что это:** Мanaged `ML` **platform**.

**ML `Workspace`:**
```yaml
# Azure ML Workspace
ml-workspace:
  name: "ml-prod-workspace"
  location: "East US 2"
  sku: "Basic"
  storageAccount: "/subscriptions/.../resourceGroups/rg-ml/providers/Microsoft.Storage/storageAccounts/stmlstorage"
  keyVault: "/subscriptions/.../resourceGroups/rg-ml/providers/Microsoft.KeyVault/vaults/kv-ml-keys"
  applicationInsights: "/subscriptions/.../resourceGroups/rg-ml/providers/Microsoft.Insights/components/ai-ml-insights"
  containerRegistry: "/subscriptions/.../resourceGroups/rg-ml/providers/Microsoft.ContainerRegistry/registries/crmlregistry"
  publicNetworkAccess: "Disabled"
  hbiWorkspace: false
  v1LegacyMode: false
```

**Compute `Targets`:**
```yaml
# AML Compute Cluster
aml-compute-cluster:
  name: "cpu-cluster"
  location: "East US 2"
  properties:
    computeType: "AmlCompute"
    properties:
      vmSize: "Standard_DS3_v2"
      vmPriority: "Dedicated"
      scaleSettings:
        minNodeCount: 0
        maxNodeCount: 4
        nodeIdleTimeBeforeScaleDown: "PT5M"
      userAccountCredentials:
        adminUserName: "azureuser"
        adminUserPassword: "{{secret}}"
      remoteLoginPortPublicAccess: "NotSpecified"
      enableNodePublicIp: false
      isolatedNetwork: false
      osType: "Linux"
      virtualMachineImage:
        id: "/subscriptions/.../resourceGroups/.../providers/Microsoft.Compute/galleries/.../images/.../versions/..."

# AML Compute Instance
aml-compute-instance:
  name: "ml-instance"
  location: "East US 2"
  properties:
    computeType: "ComputeInstance"
    properties:
      vmSize: "Standard_DS3_v2"
      enableNodePublicIp: false
      enableProxyAccess: true
      isolateNetwork: false
      osType: "Linux"
      personalComputeInstanceSettings:
        assignedUser:
          objectId: "12345678-1234-1234-1234-123456789012"
          tenantId: "12345678-1234-1234-1234-123456789012"
      sshSettings:
        sshPublicAccess: "Disabled"
      customServices: []
      subnet:
        id: "/subscriptions/.../resourceGroups/rg-ml/providers/Microsoft.Network/virtualNetworks/vnet-ml/subnets/subnet-ml"
```

### Azure Cognitive Services

**Что это: Pre-built** `AI` **services**.

**Computer `Vision`:**
```python
# Computer Vision API usage
import requests

def analyze_image(image_url):
    endpoint = "https://myvision.cognitiveservices.azure.com/"
    key = "your-subscription-key"

    analyze_url = f"{endpoint}vision/v3.2/analyze"

    headers = {
        'Ocp-Apim-Subscription-Key': key,
        'Content-Type': 'application/json'
    }

    params = {
        'visualFeatures': 'Categories,Description,Color',
        'language': 'en'
    }

    data = {'url': image_url}

    response = requests.post(analyze_url, headers=headers, params=params, json=data)
    return response.json()

# Result example
result = {
    "categories": [
        {
            "name": "outdoor_mountain",
            "score": 0.99609375
        }
    ],
    "description": {
        "tags": ["mountain", "outdoor", "nature", "snow"],
        "captions": [
            {
                "text": "a large mountain covered in snow",
                "confidence": 0.9999999403953552
            }
        ]
    },
    "color": {
        "dominantColorForeground": "White",
        "dominantColorBackground": "White",
        "dominantColors": ["White", "Grey"],
        "accentColor": "FFFFFF",
        "isBwImg": false
    }
}
```

## Developer Tools

### Azure DevOps

**Что это:** CI/CD и **project management platform**.

**Azure `DevOps Organization`:**
```yaml
# Azure DevOps Services Configuration
devops-organization:
  name: "myorg"
  location: "East US 2"
  properties:
    connectivity:
      vpn: false
      expressRoute: false
    security:
      requireMfa: true
      allowPublicProjects: false
    policies:
      allowAnonymousAccess: false
      allowTeamAdminsToInvite: true
      allowOrgAdminsToInvite: true

# Project Configuration
devops-project:
  name: "MyApp"
  description: "E-commerce application"
  visibility: "private"
  versionControl: "Git"
  workItemProcess: "Agile"
  features:
    repositories: "enabled"
    pipelines: "enabled"
    testPlans: "enabled"
    artifacts: "enabled"
```

**YAML `Pipeline`:**
```yaml
# azure-pipelines.yml
name: $(BuildDefinitionName)_$(SourceBranchName)_$(Date:yyyyMMdd)$(Rev:.r)

trigger:
  branches:
    include:
    - main
    - develop
    exclude:
    - feature/*

pr:
  branches:
    include:
    - main
    - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
  - group: 'global-variables'
  - name: 'buildConfiguration'
    value: 'Release'
  - name: 'dotnetSdkVersion'
    value: '7.0.x'

stages:
- stage: Build
  displayName: 'Build Stage'
  jobs:
  - job: Build
    displayName: 'Build and Test'
    steps:
    - task: UseDotNet@2
      displayName: 'Install .NET SDK'
      inputs:
        version: '$(dotnetSdkVersion)'

    - task: DotNetCoreCLI@2
      displayName: 'Restore NuGet packages'
      inputs:
        command: 'restore'
        projects: '/*.csproj'

    - task: DotNetCoreCLI@2
      displayName: 'Build projects'
      inputs:
        command: 'build'
        projects: '/*.csproj'
        arguments: '--configuration $(buildConfiguration)'

    - task: DotNetCoreCLI@2
      displayName: 'Run unit tests'
      inputs:
        command: 'test'
        projects: '/*Tests.csproj'
        arguments: '--configuration $(buildConfiguration) --collect "Code coverage"'

    - publish: '$(Build.ArtifactStagingDirectory)/test-results'
      artifact: 'test-results'
      condition: succeededOrFailed()

- stage: DeployStaging
  displayName: 'Deploy to Staging'
  dependsOn: Build
  condition: succeeded()
  jobs:
  - deployment: Deploy
    displayName: 'Deploy to Staging Environment'
    environment: 'staging'
    strategy:
      runOnce:
        deploy:
          steps:
          - download: current
            artifact: 'drop'
          - task: AzureRmWebAppDeployment@4
            displayName: 'Deploy to Azure Web App'
            inputs:
              azureSubscription: 'Azure-Subscription'
              appType: 'webApp'
              webAppName: 'myapp-staging'
              package: '$(Pipeline.Workspace)/drop//*.zip'
```

## Management & Governance

### Azure Policy

**Что это: Policy-as-Code** для **governance**.

**Built-in `Policies`:**
```yaml
# Storage Account Encryption Policy
storage-encryption-policy:
  name: "storage-encryption-policy"
  displayName: "Storage accounts should have encryption enabled"
  description: "Ensure all storage accounts have encryption enabled"
  mode: "Indexed"
  policyRule:
    if:
      field: "type"
      equals: "Microsoft.Storage/storageAccounts"
    then:
      effect: "Deny"
      details:
        type: "Microsoft.Storage/storageAccounts/encryption"
        name: "accountencryption"
        existenceCondition:
          field: "Microsoft.Storage/storageAccounts/encryption.services.blob.enabled"
          equals: true

# VM Security Policy
vm-security-policy:
  name: "vm-security-policy"
  displayName: "Virtual machines should have endpoint protection installed"
  description: "Ensure endpoint protection is installed on VMs"
  mode: "Indexed"
  policyRule:
    if:
      allOf:
        - field: "type"
          equals: "Microsoft.Compute/virtualMachines"
        - field: "Microsoft.Compute/virtualMachines/storageProfile.osDisk.osType"
          equals: "Windows"
    then:
      effect: "DeployIfNotExists"
      details:
        type: "Microsoft.Compute/virtualMachines/extensions"
        roleDefinitionIds:
          - "/providers/Microsoft.Authorization/roleDefinitions/b24988ac-6180-42a0-ab88-20f7382dd24c"
        existenceCondition:
          field: "Microsoft.Compute/virtualMachines/extensions/publisher"
          equals: "Microsoft.Azure.Security"
        deployment:
          properties:
            mode: "incremental"
            template:
              "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#"
              contentVersion: "1.0.0.0"
              resources:
                - type: "Microsoft.Compute/virtualMachines/extensions"
                  name: "[concat(parameters('vmName'), '/AzureSecurity')]"
                  apiVersion: "2019-12-01"
                  location: "[parameters('location')]"
                  properties:
                    publisher: "Microsoft.Azure.Security"
                    type: "AzureSecurityWindowsAgent"
                    typeHandlerVersion: "1.0"
```

### Azure Blueprints

**Что это: Declarative way** to **orchestrate deployment** of **various resource templates**.

**Blueprint `Definition`:**
```json
{
  "properties": {
    "description": "Blueprint for secure web application",
    "targetScope": "subscription",
    "parameters": {
      "principalIds": {
        "type": "array",
        "metadata": {
          "description": "Array of principal IDs to assign to the role"
        }
      },
      "webAppName": {
        "type": "string",
        "metadata": {
          "description": "Name of the web application"
        }
      }
    },
    "resourceGroups": {
      "WebAppRG": {
        "description": "Resource group for web application resources",
        "location": "East US 2"
      }
    },
    "blueprintName": "secure-web-app-blueprint"
  },
  "type": "Microsoft.Blueprint/blueprints",
  "name": "secure-web-app-blueprint"
}
```

## Лучшие практики

- **Выбор сервисов:** предпочитайте **managed**-сервисы (Azure `SQL`, `Cosmos DB`, `AKS`, App Service) для снижения операционной нагрузки; `VM` — при необходимости полного контроля или специфичного ПО.
- **Безопасность:** минимальные права **RBAC** по ролям и **scope**; управляемые идентичности вместо ключей; шифрование в покое и при передаче; **Azure Key Vault** для секретов.
- **Стоимость:** резервирования (Reserved VM Instances, Savings Plans) для стабильных нагрузок; теги и **Cost Management** для учёта по подпискам и ресурсным группам.
- **Надёжность: Availability Sets** и **Availability Zones** для критичных нагрузок; автоскейлинг и **health probes**; регулярные бэкапы и тесты восстановления.
- **Мониторинг: Azure Monitor** (метрики, логи, алерты); **Application Insights** для приложений; интеграция с **Log Analytics** и **Action Groups**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[azure-basics|Azure Basics]] — основы **Azure**
- [[azure-networking|Azure Networking]] — сеть в **Azure**
- [Azure IAM](https://docs.microsoft.com/azure/active-directory/) — управление доступом
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
- [Azure Resource Manager](https://docs.microsoft.com/azure/azure-resource-manager/management/) — управление ресурсами