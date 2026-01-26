# Azure Networking

Azure Networking предоставляет полный набор сервисов для создания масштабируемой, безопасной и высокопроизводительной сетевой инфраструктуры. Этот документ охватывает Virtual Networks, Load Balancers, Application Gateway, Front Door, VPN, ExpressRoute и другие сетевые сервисы Azure с best practices для production сред.

## Содержание
- [Virtual Networks (VNets)](#virtual-networks-vnets)
- [Subnets и Network Security Groups](#subnets-и-network-security-groups)
- [Load Balancing](#load-balancing)
- [Azure Application Gateway](#azure-application-gateway)
- [Azure Front Door](#azure-front-door)
- [Traffic Manager](#traffic-manager)
- [Virtual Network Peering](#virtual-network-peering)
- [VPN Gateway](#vpn-gateway)
- [ExpressRoute](#expressroute)
- [Azure Firewall](#azure-firewall)
- [Network Watcher](#network-watcher)
- [Private Link](#private-link)

## Virtual Networks (VNets)

### VNet Design Patterns
```yaml
# Multi-tier VNet Architecture
vnet-architecture:
  vnet:
    name: "production-vnet"
    address-space: "10.0.0.0/16"
    location: "eastus"
    ddos-protection-plan:
      id: "/subscriptions/sub-id/resourceGroups/security-rg/providers/Microsoft.Network/ddosProtectionPlans/production-ddos-plan"

  subnets:
    gateway-subnet:
      name: "GatewaySubnet"
      address-prefix: "10.0.0.0/27"
      purpose: "VPN/ExpressRoute gateways"

    bastion-subnet:
      name: "AzureBastionSubnet"
      address-prefix: "10.0.1.0/27"
      purpose: "Azure Bastion"

    firewall-subnet:
      name: "AzureFirewallSubnet"
      address-prefix: "10.0.2.0/26"
      purpose: "Azure Firewall"

    web-subnet:
      name: "web-subnet"
      address-prefix: "10.0.3.0/24"
      network-security-group: "web-nsg"
      route-table: "web-route-table"
      service-endpoints:
        - "Microsoft.Storage"
        - "Microsoft.KeyVault"
      delegations:
        - name: "Microsoft.Web/serverFarms"
          service-name: "Microsoft.Web/serverFarms"
          actions: ["Microsoft.Network/virtualNetworks/subnets/action"]

    app-subnet:
      name: "app-subnet"
      address-prefix: "10.0.4.0/24"
      network-security-group: "app-nsg"
      route-table: "app-route-table"

    data-subnet:
      name: "data-subnet"
      address-prefix: "10.0.5.0/24"
      network-security-group: "data-nsg"
      route-table: "data-route-table"
      service-endpoints:
        - "Microsoft.Sql"
        - "Microsoft.Storage"

    private-endpoint-subnet:
      name: "private-endpoint-subnet"
      address-prefix: "10.0.6.0/24"
      private-endpoint-network-policies: "Disabled"
      private-link-service-network-policies: "Disabled"
```

### VNet Service Endpoints
```yaml
# VNet Service Endpoints для secure access
service-endpoints:
  storage-endpoint:
    service: "Microsoft.Storage"
    locations: ["eastus"]
    description: "Secure access to Azure Storage"

  sql-endpoint:
    service: "Microsoft.Sql"
    locations: ["eastus"]
    description: "Secure access to Azure SQL Database"

  keyvault-endpoint:
    service: "Microsoft.KeyVault"
    locations: ["eastus"]
    description: "Secure access to Azure Key Vault"

  cosmos-endpoint:
    service: "Microsoft.AzureCosmosDB"
    locations: ["eastus"]
    description: "Secure access to Cosmos DB"
```

### VNet Integration Patterns
```yaml
# Hub-and-Spoke Architecture
hub-spoke-architecture:
  hub-vnet:
    name: "hub-vnet"
    address-space: "10.0.0.0/16"
    location: "eastus"
    subnets:
      - name: "AzureFirewallSubnet"
        address-prefix: "10.0.1.0/26"
      - name: "GatewaySubnet"
        address-prefix: "10.0.2.0/27"
      - name: "AzureBastionSubnet"
        address-prefix: "10.0.3.0/27"

  spoke-vnets:
    production-spoke:
      name: "prod-spoke-vnet"
      address-space: "10.1.0.0/16"
      peering:
        remote-vnet-id: "hub-vnet-id"
        allow-virtual-network-access: true
        allow-forwarded-traffic: true
        allow-gateway-transit: true
        use-remote-gateways: true

    development-spoke:
      name: "dev-spoke-vnet"
      address-space: "10.2.0.0/16"
      peering:
        remote-vnet-id: "hub-vnet-id"
        allow-virtual-network-access: true
        allow-forwarded-traffic: true
        allow-gateway-transit: true
        use-remote-gateways: true

  route-tables:
    spoke-route-table:
      name: "spoke-default-route"
      routes:
        - name: "default-route"
          address-prefix: "0.0.0.0/0"
          next-hop-type: "VirtualAppliance"
          next-hop-ip-address: "10.0.1.4"  # Azure Firewall IP
```

## Subnets и Network Security Groups

### Network Security Groups (NSGs)
```yaml
# NSG Design Patterns
network-security-groups:
  web-nsg:
    name: "web-nsg"
    resource-group: "network-rg"
    location: "eastus"
    security-rules:
      - name: "Allow-HTTP-Inbound"
        priority: 100
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "80"
        source-address-prefix: "*"
        destination-address-prefix: "*"

      - name: "Allow-HTTPS-Inbound"
        priority: 110
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "443"
        source-address-prefix: "*"
        destination-address-prefix: "*"

      - name: "Allow-App-Tier-Inbound"
        priority: 120
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "80"
        source-address-prefix: "10.0.4.0/24"  # App subnet
        destination-address-prefix: "*"

      - name: "Allow-Load-Balancer-Inbound"
        priority: 130
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "*"
        source-address-prefix: "AzureLoadBalancer"
        destination-address-prefix: "*"

      - name: "Deny-All-Inbound"
        priority: 4096
        direction: "Inbound"
        access: "Deny"
        protocol: "*"
        source-port-range: "*"
        destination-port-range: "*"
        source-address-prefix: "*"
        destination-address-prefix: "*"

      - name: "Allow-Outbound"
        priority: 100
        direction: "Outbound"
        access: "Allow"
        protocol: "*"
        source-port-range: "*"
        destination-port-range: "*"
        source-address-prefix: "*"
        destination-address-prefix: "*"

  app-nsg:
    name: "app-nsg"
    security-rules:
      - name: "Allow-Web-Tier-Inbound"
        priority: 100
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "8080"
        source-address-prefix: "10.0.3.0/24"  # Web subnet
        destination-address-prefix: "*"

      - name: "Allow-Data-Tier-Outbound"
        priority: 100
        direction: "Outbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "1433"
        source-address-prefix: "*"
        destination-address-prefix: "10.0.5.0/24"  # Data subnet

      - name: "Allow-Internet-Outbound"
        priority: 110
        direction: "Outbound"
        access: "Allow"
        protocol: "*"
        source-port-range: "*"
        destination-port-range: "*"
        source-address-prefix: "*"
        destination-address-prefix: "*"

  data-nsg:
    name: "data-nsg"
    security-rules:
      - name: "Allow-App-Tier-MySQL"
        priority: 100
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "3306"
        source-address-prefix: "10.0.4.0/24"  # App subnet
        destination-address-prefix: "*"

      - name: "Allow-App-Tier-PostgreSQL"
        priority: 110
        direction: "Inbound"
        access: "Allow"
        protocol: "Tcp"
        source-port-range: "*"
        destination-port-range: "5432"
        source-address-prefix: "10.0.4.0/24"
        destination-address-prefix: "*"

      - name: "Deny-All-Inbound"
        priority: 4096
        direction: "Inbound"
        access: "Deny"
        protocol: "*"
        source-port-range: "*"
        destination-port-range: "*"
        source-address-prefix: "*"
        destination-address-prefix: "*"
```

### Application Security Groups (ASGs)
```yaml
# Application Security Groups для microservices
application-security-groups:
  web-asg:
    name: "web-asg"
    location: "eastus"
    resource-group: "network-rg"

  api-asg:
    name: "api-asg"
    location: "eastus"
    resource-group: "network-rg"

  database-asg:
    name: "database-asg"
    location: "eastus"
    resource-group: "network-rg"

# NSG Rules using ASGs
asg-based-rules:
  - name: "Allow-API-to-Web"
    priority: 100
    direction: "Inbound"
    access: "Allow"
    protocol: "Tcp"
    source-port-range: "*"
    destination-port-range: "80"
    source-application-security-groups:
      - id: "api-asg-id"
    destination-application-security-groups:
      - id: "web-asg-id"

  - name: "Allow-Web-to-API"
    priority: 100
    direction: "Inbound"
    access: "Allow"
    protocol: "Tcp"
    source-port-range: "*"
    destination-port-range: "8080"
    source-application-security-groups:
      - id: "web-asg-id"
    destination-application-security-groups:
      - id: "api-asg-id"
```

## Load Balancing

### Azure Load Balancer
```yaml
# Standard Load Balancer Configuration
standard-load-balancer:
  name: "web-lb"
  location: "eastus"
  sku: "Standard"
  frontend-ip-configurations:
    - name: "PublicIPAddress"
      public-ip-address:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/publicIPAddresses/web-lb-ip"

  backend-address-pools:
    - name: "WebServers"
      load-balancer-backend-addresses:
        - name: "web-vm-1"
          virtual-network:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/production-vnet"
          ip-address: "10.0.3.10"
        - name: "web-vm-2"
          virtual-network:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/production-vnet"
          ip-address: "10.0.3.11"

  load-balancing-rules:
    - name: "HTTP"
      protocol: "Tcp"
      frontend-port: 80
      backend-port: 80
      enable-floating-ip: false
      idle-timeout-in-minutes: 4
      enable-tcp-reset: true
      load-distribution: "Default"
      frontend-ip-configuration:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/web-lb/frontendIPConfigurations/PublicIPAddress"
      backend-address-pool:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/web-lb/backendAddressPools/WebServers"
      probe:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/web-lb/probes/web-health-probe"

  probes:
    - name: "web-health-probe"
      protocol: "Http"
      port: 80
      request-path: "/health"
      interval-in-seconds: 5
      number-of-probes: 2

  outbound-rules:
    - name: "OutboundNAT"
      protocol: "All"
      frontend-ip-configurations:
        - id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/web-lb/frontendIPConfigurations/PublicIPAddress"
      backend-address-pool:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/web-lb/backendAddressPools/WebServers"
      allocated-outbound-ports: 10000
```

### Internal Load Balancer
```yaml
# Internal Load Balancer для private networks
internal-load-balancer:
  name: "internal-lb"
  location: "eastus"
  sku: "Standard"
  frontend-ip-configurations:
    - name: "PrivateIPAddress"
      private-ip-address: "10.0.4.100"
      private-ip-address-version: "IPv4"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/production-vnet/subnets/app-subnet"

  backend-address-pools:
    - name: "AppServers"

  load-balancing-rules:
    - name: "AppPort"
      protocol: "Tcp"
      frontend-port: 8080
      backend-port: 8080
      frontend-ip-configuration:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/internal-lb/frontendIPConfigurations/PrivateIPAddress"
      backend-address-pool:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/internal-lb/backendAddressPools/AppServers"
      probe:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/loadBalancers/internal-lb/probes/app-health-probe"

  probes:
    - name: "app-health-probe"
      protocol: "Tcp"
      port: 8080
      interval-in-seconds: 5
      number-of-probes: 2
```

## Azure Application Gateway

### WAF-enabled Application Gateway
```yaml
# Application Gateway v2 с WAF
application-gateway:
  name: "web-app-gateway"
  location: "eastus"
  sku:
    name: "WAF_v2"
    tier: "WAF_v2"
    capacity: 2

  gateway-ip-configurations:
    - name: "appGatewayIpConfig"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/production-vnet/subnets/app-gateway-subnet"

  frontend-ip-configurations:
    - name: "appGatewayFrontendIP"
      public-ip-address:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/publicIPAddresses/app-gateway-ip"

  frontend-ports:
    - name: "port_80"
      port: 80
    - name: "port_443"
      port: 443

  backend-address-pools:
    - name: "web-backend-pool"
      backend-addresses:
        - ip-address: "10.0.3.10"
        - ip-address: "10.0.3.11"

  backend-http-settings:
    - name: "web-backend-settings"
      port: 80
      protocol: "Http"
      cookie-based-affinity: "Disabled"
      request-timeout: 20
      probe:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/probes/web-health-probe"

  http-listeners:
    - name: "web-listener"
      frontend-ip-configuration:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/frontendIPConfigurations/appGatewayFrontendIP"
      frontend-port:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/frontendPorts/port_80"
      protocol: "Http"

    - name: "ssl-listener"
      frontend-ip-configuration:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/frontendIPConfigurations/appGatewayFrontendIP"
      frontend-port:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/frontendPorts/port_443"
      protocol: "Https"
      ssl-certificate:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/sslCertificates/ssl-cert"

  request-routing-rules:
    - name: "web-routing-rule"
      rule-type: "Basic"
      http-listener:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/httpListeners/web-listener"
      backend-address-pool:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendAddressPools/web-backend-pool"
      backend-http-settings:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendHttpSettingsCollection/web-backend-settings"

  probes:
    - name: "web-health-probe"
      protocol: "Http"
      host: "127.0.0.1"
      path: "/health"
      interval: 30
      timeout: 30
      unhealthy-threshold: 3
      pick-host-name-from-backend-http-settings: true

  ssl-certificates:
    - name: "ssl-cert"
      data: "{{ssl-certificate-data}}"
      password: "{{certificate-password}}"

  web-application-firewall-configuration:
    enabled: true
    firewall-mode: "Prevention"
    rule-set-type: "OWASP"
    rule-set-version: "3.2"
    disabled-rule-groups: []
    request-body-check: true
    max-request-body-size-in-kb: 128
    file-upload-limit-in-mb: 100
```

### URL-based Routing
```yaml
# Path-based routing
path-based-routing:
  url-path-maps:
    - name: "url-path-map"
      default-backend-address-pool:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendAddressPools/web-backend-pool"
      default-backend-http-settings:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendHttpSettingsCollection/web-backend-settings"
      path-rules:
        - name: "api-rule"
          paths:
            - "/api/*"
          backend-address-pool:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendAddressPools/api-backend-pool"
          backend-http-settings:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendHttpSettingsCollection/api-backend-settings"
        - name: "admin-rule"
          paths:
            - "/admin/*"
          backend-address-pool:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendAddressPools/admin-backend-pool"
          backend-http-settings:
            id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/backendHttpSettingsCollection/admin-backend-settings"

  request-routing-rules:
    - name: "path-based-routing"
      rule-type: "PathBasedRouting"
      http-listener:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/httpListeners/web-listener"
      url-path-map:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/applicationGateways/web-app-gateway/urlPathMaps/url-path-map"
```

## Azure Front Door

### Global Load Balancing
```yaml
# Azure Front Door Configuration
front-door:
  name: "my-front-door"
  resource-group: "cdn-rg"
  location: "Global"

  frontend-endpoints:
    - name: "my-frontend-endpoint"
      host-name: "myapp.azurefd.net"
      session-affinity-enabled-state: "Disabled"
      session-affinity-ttl-seconds: 0
      web-application-firewall-policy-link:
        id: "/subscriptions/sub-id/resourceGroups/cdn-rg/providers/Microsoft.Network/frontDoorWebApplicationFirewallPolicies/my-waf-policy"

  backend-pools:
    - name: "web-backend-pool"
      backends:
        - address: "web-app-eastus.azurewebsites.net"
          http-port: 80
          https-port: 443
          weight: 50
          priority: 1
          enabled-state: "Enabled"
          backend-host-header: "web-app-eastus.azurewebsites.net"
        - address: "web-app-westeurope.azurewebsites.net"
          http-port: 80
          https-port: 443
          weight: 50
          priority: 1
          enabled-state: "Enabled"
          backend-host-header: "web-app-westeurope.azurewebsites.net"
      load-balancing-settings:
        sample-size: 4
        successful-samples-required: 2
        additional-latency-milliseconds: 0
      health-probe-settings:
        path: "/health"
        protocol: "Https"
        interval-in-seconds: 30
        health-probe-method: "HEAD"

  routing-rules:
    - name: "web-routing-rule"
      accepted-protocols: ["Http", "Https"]
      patterns-to-match: ["/*"]
      frontend-endpoints:
        - id: "/subscriptions/sub-id/resourceGroups/cdn-rg/providers/Microsoft.Network/frontDoors/my-front-door/frontendEndpoints/my-frontend-endpoint"
      route-configuration:
        "@odata.type": "#Microsoft.Azure.FrontDoor.Models.FrontdoorForwardingConfiguration"
        forwarding-protocol: "HttpsOnly"
        backend-pool:
          id: "/subscriptions/sub-id/resourceGroups/cdn-rg/providers/Microsoft.Network/frontDoors/my-front-door/backendPools/web-backend-pool"
        cache-configuration:
          query-parameter-strip-directive: "StripNone"
          dynamic-compression: "Enabled"

  backend-pools-health-probes: []

  load-balancing-settings: []
```

### Custom Domain и SSL
```yaml
# Custom domain configuration
custom-domain-config:
  frontend-endpoints:
    - name: "custom-frontend-endpoint"
      host-name: "api.mycompany.com"
      session-affinity-enabled-state: "Disabled"
      session-affinity-ttl-seconds: 0

  custom-https-configuration:
    - custom-domain:
        id: "/subscriptions/sub-id/resourceGroups/cdn-rg/providers/Microsoft.Network/frontDoors/my-front-door/frontendEndpoints/custom-frontend-endpoint"
      certificate-source: "FrontDoor"
      minimum-tls-version: "1.2"
      certificate-type: "Dedicated"

# SSL/TLS Configuration
ssl-configuration:
  custom-https-configuration:
    - custom-domain:
        id: "/subscriptions/sub-id/resourceGroups/cdn-rg/providers/Microsoft.Network/frontDoors/my-front-door/frontendEndpoints/custom-frontend-endpoint"
      certificate-source: "AzureKeyVault"
      minimum-tls-version: "1.2"
      certificate-type: "Dedicated"
      vault-secret:
        id: "/subscriptions/sub-id/resourceGroups/security-rg/providers/Microsoft.KeyVault/vaults/my-keyvault/secrets/ssl-cert"
```

## Traffic Manager

### DNS-based Load Balancing
```yaml
# Traffic Manager Profile
traffic-manager-profile:
  name: "my-traffic-manager"
  resource-group: "traffic-rg"
  location: "global"
  profile-status: "Enabled"
  traffic-routing-method: "Performance"
  dns-config:
    relative-name: "myapp"
    ttl: 30
  monitor-config:
    protocol: "HTTPS"
    port: 443
    path: "/health"
    interval-in-seconds: 30
    timeout-in-seconds: 10
    tolerated-number-of-failures: 3

  endpoints:
    - name: "eastus-endpoint"
      type: "AzureEndpoints"
      target-resource-id: "/subscriptions/sub-id/resourceGroups/web-rg/providers/Microsoft.Network/publicIPAddresses/web-eastus-ip"
      weight: 1
      priority: 1
      endpoint-status: "Enabled"
      endpoint-location: "East US"
      min-child-endpoints: 1

    - name: "westeurope-endpoint"
      type: "AzureEndpoints"
      target-resource-id: "/subscriptions/sub-id/resourceGroups/web-rg/providers/Microsoft.Network/publicIPAddresses/web-westeurope-ip"
      weight: 1
      priority: 2
      endpoint-status: "Enabled"
      endpoint-location: "West Europe"
      min-child-endpoints: 1

    - name: "external-endpoint"
      type: "ExternalEndpoints"
      target: "web-app-external.example.com"
      weight: 1
      priority: 3
      endpoint-status: "Enabled"
      endpoint-location: "East US"
```

### Traffic Routing Methods
```yaml
# Performance-based routing
performance-routing:
  traffic-routing-method: "Performance"
  endpoints:
    - name: "east-us"
      endpoint-location: "East US"
    - name: "west-europe"
      endpoint-location: "West Europe"
    - name: "southeast-asia"
      endpoint-location: "Southeast Asia"

# Weighted routing
weighted-routing:
  traffic-routing-method: "Weighted"
  endpoints:
    - name: "primary"
      weight: 80
    - name: "secondary"
      weight: 20

# Priority routing
priority-routing:
  traffic-routing-method: "Priority"
  endpoints:
    - name: "primary"
      priority: 1
    - name: "secondary"
      priority: 2
    - name: "tertiary"
      priority: 3

# Geographic routing
geographic-routing:
  traffic-routing-method: "Geographic"
  endpoints:
    - name: "us-traffic"
      geo-mapping:
        - "US"
        - "CA"
    - name: "eu-traffic"
      geo-mapping:
        - "EU"
    - name: "default"
      geo-mapping:
        - "WORLD"
```

## Virtual Network Peering

### VNet-to-VNet Peering
```yaml
# VNet Peering Configuration
vnet-peering:
  name: "hub-to-spoke-peering"
  resource-group: "network-rg"
  virtual-network-name: "hub-vnet"

  remote-virtual-network:
    id: "/subscriptions/sub-id/resourceGroups/spoke-rg/providers/Microsoft.Network/virtualNetworks/spoke-vnet"

  allow-virtual-network-access: true
  allow-forwarded-traffic: true
  allow-gateway-transit: true
  use-remote-gateways: false

# Reverse peering (spoke to hub)
reverse-peering:
  name: "spoke-to-hub-peering"
  resource-group: "spoke-rg"
  virtual-network-name: "spoke-vnet"

  remote-virtual-network:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/hub-vnet"

  allow-virtual-network-access: true
  allow-forwarded-traffic: false
  allow-gateway-transit: false
  use-remote-gateways: true
```

### Global VNet Peering
```yaml
# Global VNet Peering между регионами
global-peering:
  eastus-vnet:
    name: "eastus-to-westeurope-peering"
    resource-group: "eastus-network-rg"
    virtual-network-name: "eastus-vnet"

    remote-virtual-network:
      id: "/subscriptions/sub-id/resourceGroups/westeurope-network-rg/providers/Microsoft.Network/virtualNetworks/westeurope-vnet"

    allow-virtual-network-access: true
    allow-forwarded-traffic: true
    allow-gateway-transit: false
    use-remote-gateways: false

  westeurope-vnet:
    name: "westeurope-to-eastus-peering"
    resource-group: "westeurope-network-rg"
    virtual-network-name: "westeurope-vnet"

    remote-virtual-network:
      id: "/subscriptions/sub-id/resourceGroups/eastus-network-rg/providers/Microsoft.Network/virtualNetworks/eastus-vnet"

    allow-virtual-network-access: true
    allow-forwarded-traffic: true
    allow-gateway-transit: false
    use-remote-gateways: false
```

## VPN Gateway

### Site-to-Site VPN
```yaml
# VPN Gateway
vpn-gateway:
  name: "vpn-gateway"
  location: "eastus"
  gateway-type: "Vpn"
  vpn-type: "RouteBased"
  sku: "VpnGw1"
  generation: "Generation1"

  ip-configurations:
    - name: "vnetGatewayConfig"
      private-ip-allocation-method: "Dynamic"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/hub-vnet/subnets/GatewaySubnet"
      public-ip-address:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/publicIPAddresses/vpn-gateway-ip"

# Local Network Gateway (on-premises)
local-network-gateway:
  name: "on-premises-lgw"
  location: "eastus"
  gateway-ip-address: "203.0.113.1"  # Public IP of on-premises VPN device
  address-space: ["192.168.0.0/16"]   # On-premises network

# VPN Connection
vpn-connection:
  name: "site-to-site-connection"
  location: "eastus"
  virtual-network-gateway1:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworkGateways/vpn-gateway"
  local-network-gateway2:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/localNetworkGateways/on-premises-lgw"
  connection-type: "IPsec"
  connection-protocol: "IKEv2"
  shared-key: "{{vpn-shared-key}}"
  enable-bgp: false
  routing-weight: 10
  ipsec-policies:
    - sa-life-time-seconds: 3600
      sa-data-size-kilobytes: 102400000
      ipsec-encryption: "AES256"
      ipsec-integrity: "SHA256"
      ike-encryption: "AES256"
      ike-integrity: "SHA256"
      dh-group: "DHGroup14"
      pfs-group: "PFS2048"
```

### Point-to-Site VPN
```yaml
# Point-to-Site VPN Configuration
p2s-vpn-gateway:
  name: "p2s-vpn-gateway"
  location: "eastus"
  gateway-type: "Vpn"
  vpn-type: "RouteBased"
  sku: "VpnGw1"
  generation: "Generation1"

  vpn-client-configuration:
    vpn-client-address-pool:
      address-prefixes: ["172.16.0.0/24"]
    vpn-client-protocols: ["OpenVPN", "IkeV2"]
    vpn-client-root-certificates:
      - name: "P2SRootCert"
        public-cert-data: "{{root-certificate-data}}"
    vpn-client-revoked-certificates: []

  ip-configurations:
    - name: "vnetGatewayConfig"
      private-ip-allocation-method: "Dynamic"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/hub-vnet/subnets/GatewaySubnet"
      public-ip-address:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/publicIPAddresses/p2s-vpn-gateway-ip"
```

## ExpressRoute

### ExpressRoute Circuit
```yaml
# ExpressRoute Circuit
expressroute-circuit:
  name: "expressroute-circuit"
  location: "eastus"
  service-provider-name: "Equinix"
  peering-location: "Equinix-Ashburn-DC2"
  bandwidth-in-mbps: 50
  sku:
    tier: "Standard"
    family: "MeteredData"
  allow-global-reach: true

# ExpressRoute Gateway
expressroute-gateway:
  name: "expressroute-gateway"
  location: "eastus"
  gateway-type: "ExpressRoute"
  sku: "Standard"
  ip-configurations:
    - name: "gatewayConfig"
      private-ip-allocation-method: "Dynamic"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/hub-vnet/subnets/GatewaySubnet"

# ExpressRoute Connection
expressroute-connection:
  name: "expressroute-connection"
  express-route-gateway-id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/expressRouteGateways/expressroute-gateway"
  express-route-circuit-peering:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/expressRouteCircuits/expressroute-circuit/peerings/AzurePrivatePeering"
  authorization-key: "{{expressroute-authorization-key}}"
  routing-weight: 10
```

### ExpressRoute Global Reach
```yaml
# ExpressRoute Global Reach
global-reach-connection:
  name: "global-reach-connection"
  express-route-circuit-peering:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/expressRouteCircuits/expressroute-circuit/peerings/AzurePrivatePeering"
  peer-express-route-circuit-peering:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/expressRouteCircuits/remote-expressroute-circuit/peerings/AzurePrivatePeering"
  address-prefix: "192.168.100.0/29"  # /29 subnet for Global Reach
  authorization-key: "{{global-reach-authorization-key}}"
```

## Azure Firewall

### Firewall Policy
```yaml
# Azure Firewall Policy
firewall-policy:
  name: "firewall-policy"
  location: "eastus"
  resource-group: "security-rg"
  sku: "Standard"

  threat-intelligence-mode: "Alert"
  threat-intelligence-allowlist:
    ip-addresses: []
    fqdns: []
    countries: []

  dns-settings:
    servers: ["208.67.222.222", "208.67.220.220"]
    enable-proxy: true

  explicit-proxy:
    enable-explicit-proxy: false
    http-port: 8080
    https-port: 8443
    pac-file-port: 8080
    pac-file: ""

  base-policy:
    id: ""  # For child policies

  snat:
    private-ranges: ["IANAPrivateRanges"]

# Firewall Rules
firewall-rules:
  application-rules:
    - name: "web-rules"
      priority: 100
      rule-collection-group: "DefaultApplicationRuleCollectionGroup"
      rules:
        - name: "allow-web"
          description: "Allow web traffic"
          protocols:
            - protocol-type: "Https"
              port: 443
          target-fqdns: ["*.microsoft.com", "*.azure.com"]
          source-addresses: ["10.0.0.0/16"]
          target-urls: []

  network-rules:
    - name: "network-rules"
      priority: 200
      rule-collection-group: "DefaultNetworkRuleCollectionGroup"
      rules:
        - name: "allow-dns"
          description: "Allow DNS"
          protocols: ["UDP"]
          source-addresses: ["10.0.0.0/16"]
          destination-addresses: ["208.67.222.222", "208.67.220.220"]
          destination-ports: ["53"]

  nat-rules:
    - name: "dnat-rules"
      priority: 100
      rule-collection-group: "DefaultDnatRuleCollectionGroup"
      rules:
        - name: "rdp-dnat"
          description: "DNAT for RDP"
          protocols: ["TCP"]
          source-addresses: ["*"]
          destination-addresses: ["firewall-public-ip"]
          destination-ports: ["3389"]
          translated-address: "10.0.3.10"
          translated-port: "3389"
```

### Azure Firewall Manager
```yaml
# Firewall Manager для centralized management
firewall-manager:
  name: "firewall-manager"
  location: "eastus"
  sku: "Standard"

  virtual-hub:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualHubs/hub-vhub"

  firewall-policy:
    id: "/subscriptions/sub-id/resourceGroups/security-rg/providers/Microsoft.Network/firewallPolicies/firewall-policy"

# Secured Virtual Hub
secured-virtual-hub:
  name: "secured-vhub"
  location: "eastus"
  virtual-hub:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualHubs/hub-vhub"
  sku: "Standard"

  security-provider-name: "AzureFirewall"
  security-partner-provider:
    id: "/subscriptions/sub-id/resourceGroups/security-rg/providers/Microsoft.Network/firewallPolicies/firewall-policy"
```

## Network Watcher

### Network Diagnostic Tools
```yaml
# Network Watcher
network-watcher:
  name: "NetworkWatcher_eastus"
  location: "eastus"
  properties: {}

# IP Flow Verify
ip-flow-verify:
  target-resource-id: "/subscriptions/sub-id/resourceGroups/vm-rg/providers/Microsoft.Compute/virtualMachines/web-vm"
  direction: "Inbound"
  protocol: "TCP"
  local-port: "80"
  remote-port: "80"
  local-ip-address: "10.0.3.10"
  remote-ip-address: "203.0.113.1"

# Next Hop
next-hop:
  target-resource-id: "/subscriptions/sub-id/resourceGroups/vm-rg/providers/Microsoft.Compute/virtualMachines/web-vm"
  source-ip-address: "10.0.3.10"
  destination-ip-address: "8.8.8.8"

# Security Group View
security-group-view:
  target-resource-id: "/subscriptions/sub-id/resourceGroups/vm-rg/providers/Microsoft.Compute/virtualMachines/web-vm"

# Packet Capture
packet-capture:
  name: "packet-capture-session"
  target: "/subscriptions/sub-id/resourceGroups/vm-rg/providers/Microsoft.Compute/virtualMachines/web-vm"
  bytes-to-capture-per-packet: 0
  total-bytes-per-session: 1073741824
  time-limit-in-seconds: 1800
  storage-location:
    storage-id: "/subscriptions/sub-id/resourceGroups/storage-rg/providers/Microsoft.Storage/storageAccounts/packetcapturestorage"
    storage-path: "https://packetcapturestorage.blob.core.windows.net/packet-captures"
    file-path: "web-vm-capture.cap"
  filters:
    - protocol: "TCP"
      local-ip-address: "10.0.3.10"
      local-port: "80"
```

### Connection Monitor
```yaml
# Connection Monitor v2
connection-monitor:
  name: "connection-monitor-v2"
  location: "eastus"
  endpoints:
    - name: "web-vm-endpoint"
      type: "AzureVM"
      resource-id: "/subscriptions/sub-id/resourceGroups/vm-rg/providers/Microsoft.Compute/virtualMachines/web-vm"
    - name: "external-endpoint"
      type: "ExternalAddress"
      address: "8.8.8.8"

  test-configurations:
    - name: "tcp-test"
      test-frequency-sec: 30
      protocol: "Tcp"
      tcp-configuration:
        port: 80
        disable-trace-route: false
      success-threshold:
        checks-failed-percent: 5
        round-trip-time-ms: 5000

    - name: "http-test"
      test-frequency-sec: 60
      protocol: "Http"
      http-configuration:
        port: 80
        method: "GET"
        path: "/health"
        request-headers:
          - name: "User-Agent"
            value: "Azure-Connection-Monitor"
        valid-status-code-ranges: ["200-299"]
      success-threshold:
        checks-failed-percent: 5
        round-trip-time-ms: 5000

  test-groups:
    - name: "web-connectivity-tests"
      disable: false
      test-configurations: ["tcp-test", "http-test"]
      sources: ["web-vm-endpoint"]
      destinations: ["external-endpoint"]
      network-watchers: []
```

## Private Link

### Private Endpoint
```yaml
# Private Endpoint для Azure Storage
private-endpoint:
  name: "storage-private-endpoint"
  location: "eastus"
  private-link-service-connections:
    - name: "storage-connection"
      private-link-service-id: "/subscriptions/sub-id/resourceGroups/storage-rg/providers/Microsoft.Storage/storageAccounts/mystorageaccount"
      group-ids: ["blob"]
      request-message: "Please approve private endpoint connection"

  subnet:
    id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/vnet/subnets/private-endpoint-subnet"

  private-dns-zone-group:
    name: "privatelink-blob-core-windows-net"
    private-dns-zone-configs:
      - name: "config1"
        private-dns-zone-id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/privateDnsZones/privatelink.blob.core.windows.net"

# Private DNS Zone
private-dns-zone:
  name: "privatelink.blob.core.windows.net"
  location: "global"
  virtual-network-links:
    - name: "vnet-link"
      virtual-network:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/vnet"
      registration-enabled: false
```

### Private Link Service
```yaml
# Private Link Service
private-link-service:
  name: "my-private-link-service"
  location: "eastus"
  load-balancer-frontend-ip-configurations:
    - id: "/subscriptions/sub-id/resourceGroups/loadbalancer-rg/providers/Microsoft.Network/loadBalancers/my-lb/frontendIPConfigurations/frontend-ip"

  ip-configurations:
    - name: "ipconfig1"
      private-ip-address: "10.0.4.10"
      private-ip-allocation-method: "Static"
      subnet:
        id: "/subscriptions/sub-id/resourceGroups/network-rg/providers/Microsoft.Network/virtualNetworks/vnet/subnets/service-subnet"

  visibility:
    subscriptions: ["external-subscription-id"]

  auto-approval:
    subscriptions: ["trusted-subscription-id"]

  fqdns: ["my-service.privatelink.azure.com"]
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Azure Networking Documentation](https://docs.microsoft.com/en-us/azure/networking/)
- [Azure Virtual Network](https://docs.microsoft.com/en-us/azure/virtual-network/)
- [Azure Load Balancer](https://docs.microsoft.com/en-us/azure/load-balancer/)
- [Azure Application Gateway](https://docs.microsoft.com/en-us/azure/application-gateway/)
- [Azure Front Door](https://docs.microsoft.com/en-us/azure/frontdoor/)
- [Azure VPN Gateway](https://docs.microsoft.com/en-us/azure/vpn-gateway/)
- [Azure ExpressRoute](https://docs.microsoft.com/en-us/azure/expressroute/)

## См. также
- [Azure Basics](azure-basics.md) - Основы Azure
- [Azure Services](azure-services.md) - Сервисы Azure
- [Azure IAM](cloud/azure-iam.md) - Управление доступом
- [Azure Resource Manager](devops/arm-templates.md) - Infrastructure as Code
- [Azure DevOps](ci-cd/azure-devops.md) - CI/CD платформа
