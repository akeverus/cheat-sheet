---
title: "Azure Basics"
description: "Microsoft Azure — облачная платформа от Microsoft, предоставляющая более 200 сервисов для создания, развертывания и управления приложениями через глобальную сеть дата-центров. Этот документ охватывает фундаментальные концепции Azure, архитектуру платформы, модели развертывания и "
tags: ["platform", "cloud-providers", "azure-basics"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Azure Basics**

**Microsoft Azure** — облачная платформа от **Microsoft**, предоставляющая более `200` сервисов для создания, развертывания и управления приложениями через глобальную сеть дата-центров. Этот документ охватывает фундаментальные концепции **Azure**, архитектуру платформы, модели развертывания и основные сервисы для начинающих пользователей.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки
- [Azure Documentation](https://docs.microsoft.com/azure)
- [Azure Free Account](https://azure.microsoft.com/free/)
- [Azure Pricing Calculator](https://azure.microsoft.com/pricing/calculator/)
- [Azure Architecture Center](https://docs.microsoft.com/azure/architecture/)
- [Azure CLI Documentation](https://docs.microsoft.com/cli/azure/)

## Содержание

- [Основы Azure](#основы-azure)
  - [Azure Architecture](#azure-architecture)
  - [Service Models](#service-models)
  - [Deployment Models](#deployment-models)
- [Azure Regions и Availability Zones](#azure-regions-и-availability-zones)
  - [Azure Regions](#azure-regions)
  - [Availability Zones](#availability-zones)
  - [SLA (Service Level Agreement)](#sla-service-level-agreement)
- [Resource Groups и Management](#resource-groups-и-management)
  - [Resource Groups](#resource-groups)
  - [Azure Subscriptions](#azure-subscriptions)
- [Azure Resource Manager](#azure-resource-manager)
  - [ARM Templates](#arm-templates)
  - [ARM Template Functions](#arm-template-functions)
  - [Bicep Templates](#bicep-templates)
- [Identity и Access Management](#identity-и-access-management)
  - [Azure Active Directory](#azure-active-directory)
  - [Role-Based Access Control (RBAC)](#role-based-access-control-rbac)
  - [Managed Identities](#managed-identities)
- [Networking Basics](#networking-basics)
  - [Virtual Networks](#virtual-networks)
  - [Load Balancers](#load-balancers)
- [Storage Basics](#storage-basics)
  - [Storage Accounts](#storage-accounts)
  - [Blob Storage](#blob-storage)
- [Compute Basics](#compute-basics)
  - [Virtual Machines](#virtual-machines)
  - [Azure App Service](#azure-app-service)
  - [Azure Functions](#azure-functions)
- [Azure CLI и PowerShell](#azure-cli-и-powershell)
  - [Azure CLI Basics](#azure-cli-basics)
  - [Azure PowerShell](#azure-powershell)
- [Monitoring и Logging](#monitoring-и-logging)
  - [Azure Monitor](#azure-monitor)
  - [Application Insights](#application-insights)
- [Cost Management](#cost-management)
  - [Azure Pricing](#azure-pricing)
  - [Azure Advisor](#azure-advisor)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы **Azure**

### Azure Architecture

Схема глобальной инфраструктуры **Microsoft Azure** (**регионы, зоны доступности, категории сервисов**).

```text
Azure Global Infrastructure:
├── Geography (География)
│   ├── North America (Северная Америка)
│   ├── Europe (Европа)
│   ├── Asia Pacific (Азиатско-Тихоокеанский регион)
│   ├── Middle East & Africa (Ближний Восток и Африка)
│   └── South America (Южная Америка)
│
├── Region (Регион) - Paris
│   ├── Availability Zones (Зоны доступности)
│   │   ├── Zone 1
│   │   ├── Zone 2
│   │   └── Zone 3
│   ├── Regional Services (Региональные сервисы)
│   └── Zonal Services (Зональные сервисы)
│
├── Azure Services Categories
│   ├── Compute (Вычисления)
│   ├── Storage (Хранение)
│   ├── Networking (Сеть)
│   ├── Databases (Базы данных)
│   ├── AI & Machine Learning
│   ├── IoT & Edge
│   ├── Security & Identity
│   ├── DevOps
│   ├── Analytics
│   └── Management & Governance
```

### Service Models
```yaml
# Infrastructure as a Service (IaaS)
iaas-services:
  description: "Полный контроль над инфраструктурой"
  examples:
    - Azure Virtual Machines
    - Azure Virtual Networks
    - Azure Storage Accounts
  responsibility:
    - application: customer
    - data: customer
    - runtime: customer
    - middleware: customer
    - os: customer
    - virtualization: azure
    - servers: azure
    - storage: azure
    - networking: azure

# Platform as a Service (PaaS)
paas-services:
  description: "Фокус на приложении, Azure управляет инфраструктурой"
  examples:
    - Azure App Service
    - Azure SQL Database
    - Azure Functions
    - Azure Kubernetes Service (AKS)
  responsibility:
    - application: customer
    - data: customer
    - runtime: customer
    - middleware: azure
    - os: azure
    - virtualization: azure
    - servers: azure
    - storage: azure
    - networking: azure

# Software as a Service (SaaS)
saas-services:
  description: "Готовое приложение"
  examples:
    - Office 365
    - Dynamics 365
    - Azure DevOps
  responsibility:
    - application: azure
    - data: customer
    - runtime: azure
    - middleware: azure
    - os: azure
    - virtualization: azure
    - servers: azure
    - storage: azure
    - networking: azure
```

### Deployment Models
```yaml
# Public Cloud
public-cloud:
  description: "Сервисы предоставляются через публичный интернет"
  advantages:
    - no_capex: true
    - high_scalability: true
    - pay_as_you_go: true
  use_cases:
    - web_applications
    - development_testing
    - big_data_analytics

# Private Cloud
private-cloud:
  description: "Инфраструктура развернута локально"
  advantages:
    - full_control: true
    - security: "максимальная"
    - compliance: true
  use_cases:
    - highly_regulated_industries
    - legacy_applications
    - data_sovereignty

# Hybrid Cloud
hybrid-cloud:
  description: "Комбинация public и private cloud"
  advantages:
    - flexibility: true
    - cost_optimization: true
    - gradual_migration: true
  use_cases:
    - lift_and_shift_migration
    - burst_computing
    - disaster_recovery
```

## Azure Regions и **Availability Zones**

### Azure Regions
```yaml
# Popular Azure Regions
azure-regions:
  - name: "East US"
    location: "Virginia, USA"
    paired_region: "West US 2"
    geography: "North America"

  - name: "West Europe"
    location: "Netherlands"
    paired_region: "North Europe"
    geography: "Europe"

  - name: "Southeast Asia"
    location: "Singapore"
    paired_region: "East Asia"
    geography: "Asia Pacific"

  - name: "Australia East"
    location: "New South Wales"
    paired_region: "Australia Southeast"
    geography: "Asia Pacific"

# Region Selection Criteria
region-selection:
  compliance:
    - data_sovereignty
    - regulatory_requirements
    - industry_standards

  performance:
    - latency_to_users
    - network_performance
    - service_availability

  cost:
    - pricing_differences
    - data_transfer_costs
    - available_services

  features:
    - service_availability
    - regional_features
    - partner_ecosystem
```

### Availability Zones
```yaml
# Availability Zone Architecture
availability-zones:
  zone_1:
    name: "Zone 1"
    datacenter: "Primary DC"
    power_source: "Grid A"
    network: "Network A"

  zone_2:
    name: "Zone 2"
    datacenter: "Secondary DC"
    power_source: "Grid B"
    network: "Network B"

  zone_3:
    name: "Zone 3"
    datacenter: "Tertiary DC"
    power_source: "Grid C"
    network: "Network C"

# Zonal Services (Zone-specific)
zonal-services:
  - virtual_machines
  - managed_disks
  - public_ip_addresses
  - load_balancers

# Zone-redundant Services (Zone-agnostic)
zone-redundant-services:
  - storage_accounts_zrs
  - sql_database_zone_redundant
  - cosmos_db
  - key_vault

# Regional Services (Region-wide)
regional-services:
  - virtual_networks
  - route_tables
  - network_security_groups
  - application_gateways
```

### SLA (`Service Level Agreement`)
```yaml
# Azure SLA Examples
service-slas:
  virtual_machines:
    single_instance: "99.9%"
    availability_set: "99.95%"
    availability_zones: "99.99%"

  storage_accounts:
    lrs: "99.999999999%"  # 11 nines
    zrs: "99.9999999999%" # 12 nines
    grs: "99.999999999999%" # 16 nines

  azure_sql_database:
    single_database: "99.99%"
    elastic_pool: "99.99%"
    managed_instance: "99.99%"

  azure_functions:
    consumption_plan: "99.95%"
    premium_plan: "99.95%"
    dedicated_plan: "99.9%"

# SLA Calculation
sla-calculation:
  serial_services: "SLA1 * SLA2 * SLA3"
  parallel_services: "1 - ((1-SLA1) * (1-SLA2) * (1-SLA3))"
  composite_sla: "min(SLA1, SLA2, SLA3)"
```

## Resource Groups и **Management**

### Resource Groups
```yaml
# Resource Group Structure
resource-group:
  name: "rg-myapp-prod"
  location: "East US"
  tags:
    Environment: "Production"
    Application: "MyApp"
    Owner: "DevOps Team"
    CostCenter: "CC-12345"
    Project: "E-commerce Platform"

  resources:
    - type: "Microsoft.Compute/virtualMachines"
      name: "vm-web-01"
    - type: "Microsoft.Network/virtualNetworks"
      name: "vnet-prod"
    - type: "Microsoft.Storage/storageAccounts"
      name: "stprodstorage"
    - type: "Microsoft.Sql/servers"
      name: "sql-prod-server"

# Resource Group Best Practices
resource-group-best-practices:
  naming:
    pattern: "rg-{application}-{environment}"
    examples:
      - "rg-myapp-prod"
      - "rg-myapp-dev"
      - "rg-shared-infra"

  organization:
    by_lifecycle: "Group resources with same lifecycle"
    by_function: "Group related resources together"
    by_environment: "Separate dev/staging/prod"
    by_region: "Consider regional boundaries"

  limits:
    resources_per_group: 800
    resource_groups_per_subscription: 980
    nested_deployments: 800
```

### Azure Subscriptions
```yaml
# Subscription Types
subscription-types:
  free:
    name: "Free Tier"
    benefits: "12 months free, $200 credit"
    limitations: "Limited services, spending limits"

  pay_as_you_go:
    name: "Pay-As-You-Go"
    benefits: "No upfront costs, pay for usage"
    target: "Development and testing"

  enterprise:
    name: "Enterprise Agreement"
    benefits: "Volume discounts, centralized billing"
    target: "Large organizations"

  student:
    name: "Student"
    benefits: "$100 credit, free services"
    target: "Students and educators"

# Subscription Design
subscription-design:
  management_groups:
    root:
      subscriptions:
        - production
        - development
        - shared_services

  policies:
    - inherit_from_management_groups
    - apply_at_subscription_level
    - enforce_resource_consistency

  rbac:
    - subscription_owners
    - subscription_contributors
    - resource_group_owners
```

## Azure Resource Manager

### ARM Templates
```json
// Basic ARM Template Structure
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "storageAccountName": {
      "type": "string",
      "metadata": {
        "description": "Name of the storage account"
      }
    },
    "location": {
      "type": "string",
      "defaultValue": "[resourceGroup().location]",
      "metadata": {
        "description": "Location for all resources"
      }
    }
  },
  "variables": {
    "storageAccountSku": "Standard_LRS"
  },
  "resources": [
    {
      "type": "Microsoft.Storage/storageAccounts",
      "apiVersion": "2021-09-01",
      "name": "[parameters('storageAccountName')]",
      "location": "[parameters('location')]",
      "sku": {
        "name": "[variables('storageAccountSku')]"
      },
      "kind": "StorageV2",
      "properties": {
        "supportsHttpsTrafficOnly": true,
        "minimumTlsVersion": "TLS1_2"
      }
    }
  ],
  "outputs": {
    "storageAccountName": {
      "type": "string",
      "value": "[parameters('storageAccountName')]"
    },
    "storageAccountId": {
      "type": "string",
      "value": "[resourceId('Microsoft.Storage/storageAccounts', parameters('storageAccountName'))]"
    }
  }
}
```

### ARM Template Functions
```json
// Common ARM Functions
arm-functions:
  resourceId:
    syntax: "[resourceId(resourceType, resourceName)]"
    example: "[resourceId('Microsoft.Storage/storageAccounts', parameters('storageAccountName'))]"

  reference:
    syntax: "[reference(resourceName, apiVersion)]"
    example: "[reference(parameters('storageAccountName'), '2021-09-01').primaryEndpoints.blob]"

  concat:
    syntax: "[concat(string1, string2, ...)]"
    example: "[concat('rg-', parameters('environment'), '-storage')]"

  uniqueString:
    syntax: "[uniqueString(baseString)]"
    example: "[uniqueString(resourceGroup().id)]"

  resourceGroup:
    syntax: "[resourceGroup()]"
    properties:
      - id
      - name
      - location
      - tags

  subscription:
    syntax: "[subscription()]"
    properties:
      - subscriptionId
      - tenantId
      - displayName
```

### Bicep Templates
```bicep
// Bicep Template Example (simplified ARM)
param storageAccountName string
param location string = resourceGroup().location

var storageAccountSku = 'Standard_LRS'

resource storageAccount 'Microsoft.Storage/storageAccounts@2021-09-01' = {
  name: storageAccountName
  location: location
  sku: {
    name: storageAccountSku
  }
  kind: 'StorageV2'
  properties: {
    supportsHttpsTrafficOnly: true
    minimumTlsVersion: 'TLS1_2'
  }
}

output storageAccountName string = storageAccount.name
output storageAccountId string = storageAccount.id
```

## Identity и **Access Management**

### Azure Active Directory
```yaml
# Azure AD Structure
azure-ad-structure:
  tenant:
    description: "Azure AD instance"
    id: "tenant-id"
    domains:
      - "company.onmicrosoft.com"
      - "company.com"

  users:
    types:
      - member: "Employee accounts"
      - guest: "External users"
    authentication:
      - password
      - multi_factor
      - certificate
      - social_accounts

  groups:
    types:
      - security: "Access control"
      - office_365: "Collaboration"
      - distribution: "Email distribution"
    membership:
      - assigned: "Manual assignment"
      - dynamic: "Rule-based"

  applications:
    - web_applications
    - mobile_applications
    - api_applications
    - service_principals
```

### Role-Based Access Control (**RBAC**)
```yaml
# Built-in Azure Roles
built-in-roles:
  owner:
    permissions: "Full access to all resources"
    scope: "Management group, subscription, resource group, resource"

  contributor:
    permissions: "Create and manage all types of Azure resources"
    scope: "Same as Owner but cannot grant access"

  reader:
    permissions: "View all resources but cannot make changes"
    scope: "All scopes"

  user_access_administrator:
    permissions: "Manage user access to Azure resources"
    scope: "All scopes"

  virtual_machine_contributor:
    permissions: "Create and manage virtual machines"
    scope: "Resource group, resource"

# Custom Role Definition
custom-role:
  name: "Database Administrator"
  description: "Can manage Azure SQL databases"
  assignableScopes:
    - "/subscriptions/subscription-id"
  permissions:
    - actions:
      - "Microsoft.Sql/servers/databases/*"
        - "Microsoft.Sql/servers/elasticPools/*"
    notActions:
      - "Microsoft.Sql/servers/databases/delete"
    dataActions:
      - "Microsoft.Sql/servers/databases/backup/action"
    notDataActions: []
```

### Managed Identities
```yaml
# System-assigned Managed Identity
system-managed-identity:
  description: "Automatically created and tied to resource lifecycle"
  use_cases:
    - app_service_access_key_vault
    - vm_access_storage_account
    - function_access_cosmos_db

  example:
    resource: "Microsoft.Web/sites"
    identity:
      type: "SystemAssigned"

# User-assigned Managed Identity
user-managed-identity:
  description: "Created separately, can be shared across resources"
  use_cases:
    - shared_access_across_resources
    - cross_subscription_access
    - custom_lifecycle_management

  example:
    resource: "Microsoft.ManagedIdentity/userAssignedIdentities"
    name: "my-shared-identity"
    location: "East US"
```

## Networking Basics

### Virtual Networks
```yaml
# Virtual Network Configuration
virtual-network:
  name: "vnet-prod"
  addressSpace:
    addressPrefixes:
      - "10.0.0.0/16"
  subnets:
    - name: "subnet-web"
      addressPrefix: "10.0.1.0/24"
      networkSecurityGroup: "nsg-web"
    - name: "subnet-app"
      addressPrefix: "10.0.2.0/24"
      networkSecurityGroup: "nsg-app"
    - name: "subnet-data"
      addressPrefix: "10.0.3.0/24"
      networkSecurityGroup: "nsg-data"

  dnsServers:
    - "168.63.129.16"  # Azure DNS
    - "8.8.8.8"        # Google DNS

# Network Security Group
network-security-group:
  name: "nsg-web"
  securityRules:
    - name: "AllowHTTP"
      priority: 100
      direction: "Inbound"
      access: "Allow"
      protocol: "Tcp"
      sourcePortRange: "*"
      destinationPortRange: "80"
      sourceAddressPrefix: "*"
      destinationAddressPrefix: "*"

    - name: "AllowHTTPS"
      priority: 110
      direction: "Inbound"
      access: "Allow"
      protocol: "Tcp"
      sourcePortRange: "*"
      destinationPortRange: "443"
      sourceAddressPrefix: "*"
      destinationAddressPrefix: "*"
```

### Load Balancers
```yaml
# Azure Load Balancer
load-balancer:
  name: "lb-web"
  sku: "Standard"
  frontendIpConfigurations:
    - name: "frontend-ip"
      publicIpAddress:
        id: "/subscriptions/.../publicIPAddresses/lb-public-ip"
  backendAddressPools:
    - name: "backend-pool"
  loadBalancingRules:
    - name: "http-rule"
      protocol: "Tcp"
      frontendPort: 80
      backendPort: 80
      frontendIpConfiguration:
        id: "/subscriptions/.../frontendIPConfigurations/frontend-ip"
      backendAddressPool:
        id: "/subscriptions/.../backendAddressPools/backend-pool"
  probes:
    - name: "http-probe"
      protocol: "Http"
      port: 80
      requestPath: "/health"
      intervalInSeconds: 5
      numberOfProbes: 2

# Application Gateway
application-gateway:
  name: "agw-web"
  sku:
    name: "Standard_v2"
    tier: "Standard_v2"
    capacity: 2
  gatewayIpConfigurations:
    - name: "appGatewayIpConfig"
      subnet:
        id: "/subscriptions/.../subnets/subnet-agw"
  frontendIpConfigurations:
    - name: "appGatewayFrontendIP"
      publicIPAddress:
        id: "/subscriptions/.../publicIPAddresses/agw-public-ip"
  frontendPorts:
    - name: "port_80"
      port: 80
    - name: "port_443"
      port: 443
  backendAddressPools:
    - name: "appGatewayBackendPool"
  backendHttpSettingsCollection:
    - name: "appGatewayBackendHttpSettings"
      port: 80
      protocol: "Http"
      cookieBasedAffinity: "Disabled"
  httpListeners:
    - name: "appGatewayHttpListener"
      frontendIpConfiguration:
        id: "/subscriptions/.../frontendIPConfigurations/appGatewayFrontendIP"
      frontendPort:
        id: "/subscriptions/.../frontendPorts/port_80"
      protocol: "Http"
  requestRoutingRules:
    - name: "rule1"
      ruleType: "Basic"
      httpListener:
        id: "/subscriptions/.../httpListeners/appGatewayHttpListener"
      backendAddressPool:
        id: "/subscriptions/.../backendAddressPools/appGatewayBackendPool"
      backendHttpSettings:
        id: "/subscriptions/.../backendHttpSettingsCollection/appGatewayBackendHttpSettings"
```

## Storage Basics

### Storage Accounts
```yaml
# Storage Account Types
storage-account-types:
  general_purpose_v2:
    description: "Most versatile, recommended for most scenarios"
    features:
      - blobs
      - files
      - queues
      - tables
      - disks
    redundancy: "LRS, GRS, ZRS, GZRS"

  general_purpose_v1:
    description: "Legacy, use v2 for new deployments"
    limitations: "No access tiers, limited features"

  blob_storage:
    description: "Blob-only storage"
    use_case: "Large amounts of unstructured data"

  file_storage:
    description: "File-only storage"
    use_case: "Lift and shift file shares"

  block_blob_storage:
    description: "Block blob-only storage"
    use_case: "High-throughput scenarios"

# Storage Account Configuration
storage-account:
  name: "stprodstorage"
  kind: "StorageV2"
  sku:
    name: "Standard_RAGRS"  # Read-access geo-redundant
  location: "East US"
  allowBlobPublicAccess: false
  minimumTlsVersion: "TLS1_2"
  supportsHttpsTrafficOnly: true
  networkAcls:
    defaultAction: "Deny"
    virtualNetworkRules:
      - virtualNetworkResourceId: "/subscriptions/.../virtualNetworks/vnet-prod"
        action: "Allow"
    ipRules:
      - value: "203.0.113.1"
        action: "Allow"

# Access Tiers
access-tiers:
  hot:
    description: "Frequently accessed data"
    cost: "Higher storage, lower access"
    availability: "99.9%"

  cool:
    description: "Infrequently accessed data (30+ days)"
    cost: "Lower storage, higher access"
    availability: "99%"

  archive:
    description: "Rarely accessed data (180+ days)"
    cost: "Lowest storage, highest access"
    availability: "99%"
    retrieval_time: "1-15 hours"
```

### Blob Storage
```yaml
# Blob Containers
blob-containers:
  - name: "images"
    publicAccess: "blob"
    metadata:
      category: "user-uploads"
      retention: "365"

  - name: "logs"
    publicAccess: "none"
    metadata:
      category: "application-logs"
      retention: "90"

  - name: "backups"
    publicAccess: "none"
    metadata:
      category: "database-backups"
      retention: "2555"  # 7 years

# Blob Types
blob-types:
  block-blobs:
    description: "Text and binary data up to 4.75 TB"
    use_cases:
      - files
      - images
      - videos
      - documents

  append-blobs:
    description: "Optimized for append operations"
    use_cases:
      - logging
      - audit trails

  page-blobs:
    description: "Random read/write operations"
    use_cases:
      - vhds
      - os_disks
      - application_disks
```

## Compute Basics

### Virtual Machines
```yaml
# VM Sizes
vm-sizes:
  general-purpose:
    - B-series: "Burstable, cost-effective"
    - D-series: "General purpose, balanced"
    - A-series: "Legacy, basic"

  compute-optimized:
    - F-series: "High CPU-to-memory ratio"
    - FX-series: "High frequency"

  memory-optimized:
    - E-series: "High memory-to-CPU ratio"
    - M-series: "Largest memory instances"

  storage-optimized:
    - L-series: "High disk throughput"
    - LS-series: "Local SSD storage"

  gpu-enabled:
    - N-series: "GPU instances"
    - NV-series: "GPU visualization"
    - NC-series: "GPU compute"

# VM Configuration
virtual-machine:
  name: "vm-web-01"
  location: "East US"
  size: "Standard_DS1_v2"
  osDisk:
    name: "vm-web-01-osdisk"
    caching: "ReadWrite"
    createOption: "FromImage"
    managedDisk:
      storageAccountType: "Premium_LRS"
  imageReference:
    publisher: "Canonical"
    offer: "UbuntuServer"
    sku: "18.04-LTS"
    version: "latest"
  osProfile:
    computerName: "vm-web-01"
    adminUsername: "azureuser"
    linuxConfiguration:
      disablePasswordAuthentication: true
      ssh:
        publicKeys:
          - path: "/home/azureuser/.ssh/authorized_keys"
            keyData: "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC..."
  networkProfile:
    networkInterfaces:
      - id: "/subscriptions/.../networkInterfaces/vm-web-01-nic"
```

### Azure App Service
```yaml
# App Service Plan
app-service-plan:
  name: "asp-prod"
  location: "East US"
  sku:
    name: "P1V2"  # Premium V2
    tier: "PremiumV2"
    size: "P1V2"
    capacity: 2
  properties:
    perSiteScaling: false
    maximumElasticWorkerCount: 1
    isSpot: false
    spotExpirationTime: null
    freeOfferExpirationTime: null
    hyperV: false
    targetWorkerCount: 0
    targetWorkerSizeId: 0

# Web App
web-app:
  name: "myapp-prod"
  location: "East US"
  serverFarmId: "/subscriptions/.../serverFarms/asp-prod"
  siteConfig:
    linuxFxVersion: "NODE|16-lts"
    alwaysOn: true
    http20Enabled: true
    minTlsVersion: "1.2"
    ftpsState: "Disabled"
    healthCheckPath: "/health"
    use32BitWorkerProcess: false
    webSocketsEnabled: false
    cors:
      allowedOrigins:
        - "https://myapp.com"
  identity:
    type: "SystemAssigned"
```

### Azure Functions
```yaml
# Function App
function-app:
  name: "func-prod"
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

# Function Configuration
function-config:
  name: "HttpTrigger"
  config:
    bindings:
      - name: "req"
        type: "httpTrigger"
        direction: "in"
        authLevel: "function"
        methods:
          - "get"
          - "post"
      - name: "res"
        type: "http"
        direction: "out"
    disabled: false
    scriptFile: "index.js"
```

## Azure CLI и **PowerShell**

### Azure CLI Basics
```bash
# Login and Authentication
az login --use-device-code
az account set --subscription "My Subscription"
az account list --output table

# Resource Group Management
az group create --name rg-myapp --location eastus
az group list --output table
az group delete --name rg-myapp --yes

# VM Management
az vm create \
  --resource-group rg-myapp \
  --name vm-web \
  --image Ubuntu2204 \
  --admin-username azureuser \
  --generate-ssh-keys \
  --public-ip-sku Standard

az vm list --resource-group rg-myapp --output table
az vm start --resource-group rg-myapp --name vm-web
az vm stop --resource-group rg-myapp --name vm-web

# Storage Account
az storage account create \
  --name stmyappstorage \
  --resource-group rg-myapp \
  --location eastus \
  --sku Standard_LRS \
  --kind StorageV2

# Networking
az network vnet create \
  --resource-group rg-myapp \
  --name vnet-myapp \
  --address-prefix 10.0.0.0/16 \
  --subnet-name subnet-web \
  --subnet-prefix 10.0.1.0/24
```

### Azure PowerShell
```powershell
# Login and Authentication
Connect-AzAccount
Set-AzContext -Subscription "My Subscription"
Get-AzContext

# Resource Group Management
New-AzResourceGroup -Name "rg-myapp" -Location "East US"
Get-AzResourceGroup | Format-Table
Remove-AzResourceGroup -Name "rg-myapp" -Force

# VM Management
$vmParams = @{
    ResourceGroupName = "rg-myapp"
    Name = "vm-web"
    Image = "Ubuntu2204"
    AdminUsername = "azureuser"
    GenerateSshKey = $true
    PublicIpSku = "Standard"
}
New-AzVM @vmParams

Get-AzVM -ResourceGroupName "rg-myapp" | Format-Table
Start-AzVM -ResourceGroupName "rg-myapp" -Name "vm-web"
Stop-AzVM -ResourceGroupName "rg-myapp" -Name "vm-web"

# Storage Account
$storageParams = @{
    ResourceGroupName = "rg-myapp"
    Name = "stmyappstorage"
    Location = "East US"
    SkuName = "Standard_LRS"
    Kind = "StorageV2"
}
New-AzStorageAccount @storageParams
```

## Monitoring и **Logging**

### Azure Monitor
```yaml
# Azure Monitor Components
azure-monitor:
  metrics:
    description: "Numeric data from resources"
    retention: "93 days (free), 1-5 years (paid)"

  logs:
    description: "Structured log data"
    retention: "30-730 days"

  alerts:
    description: "Notifications based on metrics/logs"
    types:
      - metric_alerts
      - log_alerts
      - activity_log_alerts

  insights:
    description: "Pre-built monitoring solutions"
    examples:
      - Application Insights
      - Container Insights
      - VM Insights

# Log Analytics Workspace
log-analytics-workspace:
  name: "law-prod"
  location: "East US"
  sku: "PerGB2018"
  retentionInDays: 90
  features:
    enableLogAccessUsingOnlyResourcePermissions: true

# Diagnostic Settings
diagnostic-settings:
  name: "diagnostic-settings"
  logs:
    - category: "AuditEvent"
      enabled: true
      retentionPolicy:
        days: 90
      enabled: true
    - category: "Administrative"
      enabled: true
    - category: "Security"
      enabled: true
  metrics:
    - category: "AllMetrics"
      enabled: true
      retentionPolicy:
        days: 90
        enabled: true
  workspaceId: "/subscriptions/.../resourceGroups/.../providers/Microsoft.OperationalInsights/workspaces/law-prod"
```

### Application Insights
```yaml
# Application Insights Configuration
application-insights:
  name: "ai-myapp"
  location: "East US"
  kind: "web"
  applicationType: "web"
  retentionInDays: 90
  samplingPercentage: 100
  features:
    enableLiveMetrics: true
    enableProfiler: true
    enableSnapshotDebugger: true

# Application Settings for Web App
app-settings:
  - name: "APPINSIGHTS_INSTRUMENTATIONKEY"
    value: "@Microsoft.KeyVault(SecretUri=https://myvault.vault.azure.net/secrets/ai-key/)"
  - name: "APPLICATIONINSIGHTS_CONNECTION_STRING"
    value: "@Microsoft.KeyVault(SecretUri=https://myvault.vault.azure.net/secrets/ai-connection-string/)"

# SDK Integration (Node.js example)
sdk-integration:
  npm: "npm install applicationinsights"
  code: |
    const appInsights = require('applicationinsights');
    appInsights.setup(process.env.APPLICATIONINSIGHTS_CONNECTION_STRING)
      .setAutoDependencyCorrelation(true)
      .setAutoCollectRequests(true)
      .setAutoCollectPerformance(true)
      .setAutoCollectExceptions(true)
      .setAutoCollectDependencies(true)
      .setAutoCollectConsole(true)
      .setUseDiskRetryCaching(true)
      .start();
```

## Cost Management

### Azure Pricing
```yaml
# Pricing Models
pricing-models:
  pay_as_you_go:
    description: "Pay for what you use, no upfront costs"
    billing: "Monthly based on usage"
    commitment: "None"

  reservations:
    description: "Pre-purchase compute capacity"
    discount: "Up to 72% savings"
    commitment: "1-3 years"

  spot_instances:
    description: "Use unused capacity at discount"
    discount: "Up to 90% savings"
    availability: "Variable"

  azure_hybrid_benefit:
    description: "Use existing Windows/Linux licenses"
    discount: "Up to 40% savings"
    requirement: "Active Software Assurance"

# Cost Management Tools
cost-management-tools:
  cost_analysis:
    description: "Analyze and monitor costs"
    features:
      - daily_cost_tracking
      - budget_alerts
      - cost_by_resource
      - cost_forecasting

  budgets:
    description: "Set spending limits and alerts"
    types:
      - cost_budget
      - usage_budget
  alerts:
      - actual_cost: "80%, 90%, 100%"
      - forecasted_cost: "80%, 90%, 100%"

  cost_management_exports:
    description: "Export cost data to storage"
    formats:
      - csv
      - json
    destinations:
      - storage_account
      - synapse_analytics

# Cost Optimization
cost-optimization:
  rightsizing:
    description: "Choose correct VM sizes"
    tools: ["Azure Advisor", "Cost Analysis"]

  reserved_instances:
    description: "Pre-purchase for steady usage"
    recommendation: "For 1+ year steady workloads"

  auto_shutdown:
    description: "Shutdown non-production resources"
    automation: "Azure Automation", "Azure Policy"

  storage_optimization:
    description: "Use appropriate storage tiers"
    strategy: "Hot → Cool → Archive lifecycle"
```

### Azure Advisor
```yaml
# Azure Advisor Recommendations
advisor-recommendations:
  cost:
    - unused_resources
    - reserved_instances
    - storage_optimization
    - vm_rightsizing

  security:
    - network_security_groups
    - encryption
    - access_management
    - threat_detection

  reliability:
    - backup_configuration
    - availability_zones
    - disaster_recovery
    - health_monitoring

  performance:
    - vm_sku_optimization
    - storage_performance
    - database_performance
    - network_optimization

  operational_excellence:
    - monitoring_setup
    - automation
    - resource_tagging
    - policy_compliance
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [Azure Services](azure-services.md) — сервисы **Azure**
- [AWS Basics](aws-basics.md) — основы **AWS**
- [GCP Basics](gcp-basics.md) — основы **GCP**
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
- [Azure Resource Manager](https://docs.microsoft.com/azure/azure-resource-manager/management/) — управление ресурсами