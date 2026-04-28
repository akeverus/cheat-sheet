---
title: "AWS Networking"
description: "AWS Networking предоставляет полный набор сетевых сервисов для создания масштабируемой, безопасной и высокопроизводительной сетевой инфраструктуры. Этот документ охватывает VPC (Virtual Private Cloud), подсети, security groups, load balancers, DNS, CDN и другие сетевые компоненты"
tags:
  - platform
  - cloud-providers
  - aws-networking
type: "overview"
difficulty: "intermediate"
aliases:
  - "AWS Networking"
prerequisites:
  - "[[aws-basics]]"
next:
  - "[[aws-services]]"
updated: "2026-04-20"
---
# AWS Networking

**AWS Networking** предоставляет полный набор сетевых сервисов для создания масштабируемой, безопасной и высокопроизводительной сетевой инфраструктуры. Этот документ охватывает **VPC** (`Virtual Private Cloud`), подсети, **security groups**, **load balancers**, **DNS**, **CDN** и другие сетевые компоненты **AWS** с **best practices** для **enterprise** сред.

## Полезные ссылки
- [VPC Documentation](https://docs.aws.amazon.com/vpc/)
- [Load Balancing](https://docs.aws.amazon.com/elasticloadbalancing/)
- [Route 53](https://docs.aws.amazon.com/route53/)
- [CloudFront](https://docs.aws.amazon.com/cloudfront/)
- [Direct Connect](https://docs.aws.amazon.com/directconnect/)
- [VPN](https://docs.aws.amazon.com/vpn/)


### См. также
- [Azure Services](azure-services.md)
## Содержание

- [VPC (Virtual Private Cloud)](#vpc-virtual-private-cloud)
  - [VPC Architecture](#vpc-architecture)
  - [VPC Creation и Configuration](#vpc-creation-и-configuration)
  - [VPC Flow Logs](#vpc-flow-logs)
- [Subnets и Availability Zones](#subnets-и-availability-zones)
  - [Subnet Design](#subnet-design)
  - [Reserved IPs и Network Planning](#reserved-ips-и-network-planning)
- [Security Groups и NACLs](#security-groups-и-nacls)
  - [Security Groups](#security-groups)
  - [Network ACLs](#network-acls)
- [Internet Gateway и NAT Gateway](#internet-gateway-и-nat-gateway)
  - [Internet Gateway](#internet-gateway)
  - [NAT Gateway](#nat-gateway)
  - [Gateway Load Balancer (GWLB)](#gateway-load-balancer-gwlb)
- [VPC Peering и Transit Gateway](#vpc-peering-и-transit-gateway)
  - [VPC Peering](#vpc-peering)
  - [Transit Gateway](#transit-gateway)
  - [AWS Resource Access Manager (RAM)](#aws-resource-access-manager-ram)
- [Load Balancing](#load-balancing)
  - [Application Load Balancer (ALB)](#application-load-balancer-alb)
  - [Network Load Balancer (NLB)](#network-load-balancer-nlb)
  - [Load Balancer Best Practices](#load-balancer-best-practices)
- [Route 53 (DNS)](#route-53-dns)
  - [Hosted Zones](#hosted-zones)
  - [Routing Policies](#routing-policies)
  - [Health Checks и Failover](#health-checks-и-failover)
- [CloudFront (CDN)](#cloudfront-cdn)
  - [Distribution Configuration](#distribution-configuration)
  - [Lambda@Edge](#lambdaedge)
  - [CloudFront Functions](#cloudfront-functions)
- [Direct Connect и VPN](#direct-connect-и-vpn)
  - [Direct Connect](#direct-connect)
  - [Site-to-Site VPN](#site-to-site-vpn)
  - [Client VPN](#client-vpn)
- [Network Monitoring и Security](#network-monitoring-и-security)
  - [VPC Reachability Analyzer](#vpc-reachability-analyzer)
  - [Network Firewall](#network-firewall)
  - [Traffic Mirroring](#traffic-mirroring)
  - [Network ACL Monitoring](#network-acl-monitoring)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## VPC (`Virtual Private Cloud`)

### VPC Architecture
```yaml
# VPC Design Patterns
vpc-patterns:
  single-vpc:
    description: "Простая VPC для development/testing"
    cidr: "10.0.0.0/16"
    subnets:
      - public: "10.0.1.0/24"
      - private: "10.0.2.0/24"

  multi-tier-vpc:
    description: "Multi-tier architecture с разделением"
    cidr: "10.0.0.0/16"
    tiers:
      - web: "10.0.1.0/24"      # Public
      - app: "10.0.2.0/24"      # Private
      - data: "10.0.3.0/24"     # Private
      - management: "10.0.4.0/24" # Private

  hub-and-spoke:
    description: "Centralized VPC with spoke VPCs"
    hub-vpc: "10.0.0.0/16"
    spoke-vpcs:
      - dev: "10.1.0.0/16"
      - staging: "10.2.0.0/16"
      - prod: "10.3.0.0/16"
```

### VPC Creation и Configuration
```yaml
# VPC с IPv4 и IPv6
vpc-config:
  vpc:
    name: production-vpc
    cidr-block: 10.0.0.0/16
    ipv6-cidr-block: 2001:db8:1234:1a00::/56
    enable-dns-hostnames: true
    enable-dns-support: true
    instance-tenancy: default

  dhcp-options:
    name: production-dhcp
    domain-name: ec2.internal
    domain-name-servers:
      - AmazonProvidedDNS
    ntp-servers:
      - 169.254.169.123

  tags:
    - key: Environment
      value: production
    - key: Project
      value: my-app
    - key: Owner
      value: network-team
```

### VPC Flow Logs
```yaml
# VPC Flow Logs для monitoring
vpc-flow-logs:
  log-destination-type: s3
  log-destination: arn:aws:s3:::my-vpc-flow-logs-bucket/logs/
  traffic-type: ALL
  log-format: |
    ${version} ${account-id} ${interface-id} ${srcaddr} ${dstaddr} ${srcport} ${dstport} ${protocol} ${packets} ${bytes} ${start} ${end} ${action} ${log-status}

# CloudWatch Logs destination
vpc-flow-logs-cloudwatch:
  log-destination-type: cloud-watch-logs
  log-destination: arn:aws:logs:region:account:log-group:vpc-flow-logs
  traffic-type: REJECT
  max-aggregation-interval: 60

# Athena query для анализа flow logs
athena-queries:
  top-talkers: |
    SELECT srcaddr, SUM(bytes) as total_bytes
    FROM vpc_flow_logs
    WHERE action = 'ACCEPT'
    GROUP BY srcaddr
    ORDER BY total_bytes DESC
    LIMIT 10

  security-analysis: |
    SELECT srcaddr, dstaddr, dstport, action, COUNT(*) as connections
    FROM vpc_flow_logs
    WHERE dstport IN (22, 3389, 80, 443) AND action = 'REJECT'
    GROUP BY srcaddr, dstaddr, dstport, action
    ORDER BY connections DESC
```

## Subnets и Availability Zones

### Subnet Design
```yaml
# Subnet Architecture
subnet-architecture:
  vpc-cidr: 10.0.0.0/16

  public-subnets:
    - name: public-a
      cidr: 10.0.1.0/24
      az: us-east-1a
      type: public
      routes:
        - destination: 0.0.0.0/0
          target: igw-12345

    - name: public-b
      cidr: 10.0.2.0/24
      az: us-east-1b
      type: public

    - name: public-c
      cidr: 10.0.3.0/24
      az: us-east-1c
      type: public

  private-subnets:
    - name: app-a
      cidr: 10.0.10.0/24
      az: us-east-1a
      type: private
      routes:
        - destination: 0.0.0.0/0
          target: nat-12345

    - name: app-b
      cidr: 10.0.11.0/24
      az: us-east-1b
      type: private

    - name: data-a
      cidr: 10.0.20.0/24
      az: us-east-1a
      type: private-data

    - name: data-b
      cidr: 10.0.21.0/24
      az: us-east-1b
      type: private-data
```

### Reserved IPs и Network Planning
```yaml
# IP Address Management
ip-management:
  vpc-cidr: 10.0.0.0/16  # 65,536 addresses

  reserved-ranges:
    - aws-reserved: 10.0.0.0/20    # AWS reserved (4,096 addresses)
    - custom-reserved: 10.0.255.0/24  # Custom reserved

  subnet-allocation:
    public-subnets: 10.0.1.0/24 - 10.0.3.0/24    # 3 subnets × 256 addresses
    private-app: 10.0.10.0/24 - 10.0.13.0/24     # 4 subnets × 256 addresses
    private-data: 10.0.20.0/24 - 10.0.23.0/24     # 4 subnets × 256 addresses
    spare: 10.0.100.0/22                          # Future expansion

  host-allocation:
    gateway: x.x.x.1         # VPC Router
    broadcast: x.x.x.255     # Broadcast (if applicable)
    dns: x.x.x.2            # DNS Server
    reserved: x.x.x.3-10     # AWS reserved
    available: x.x.x.11-254  # Available for instances
```

## Security Groups и NACLs

### Security Groups
```yaml
# Security Group для Web Servers
web-security-group:
  name: web-servers-sg
  description: Security group for web servers
  vpc-id: vpc-12345678

  ingress-rules:
    - description: HTTP from anywhere
      from-port: 80
      to-port: 80
      protocol: tcp
      cidr-blocks: ["0.0.0.0/0"]

    - description: HTTPS from anywhere
      from-port: 443
      to-port: 443
      protocol: tcp
      cidr-blocks: ["0.0.0.0/0"]

    - description: SSH from bastion
      from-port: 22
      to-port: 22
      protocol: tcp
      security-groups: ["sg-bastion123"]

  egress-rules:
    - description: All outbound traffic
      from-port: 0
      to-port: 0
      protocol: -1
      cidr-blocks: ["0.0.0.0/0"]

# Security Group для Application Servers
app-security-group:
  name: app-servers-sg
  description: Security group for application servers

  ingress-rules:
    - description: HTTP from web servers
      from-port: 8080
      to-port: 8080
      protocol: tcp
      security-groups: ["sg-web123"]

    - description: Health checks from ALB
      from-port: 8080
      to-port: 8080
      protocol: tcp
      security-groups: ["sg-alb123"]

  egress-rules:
    - description: To database
      from-port: 3306
      to-port: 3306
      protocol: tcp
      security-groups: ["sg-db123"]

    - description: HTTPS outbound
      from-port: 443
      to-port: 443
      protocol: tcp
      cidr-blocks: ["0.0.0.0/0"]
```

### Network ACLs
```yaml
# Network ACL для Public Subnets
public-nacl:
  name: public-subnet-nacl
  vpc-id: vpc-12345678
  subnet-ids:
    - subnet-public-a
    - subnet-public-b
    - subnet-public-c

  ingress:
    - rule-number: 100
      protocol: tcp
      from-port: 80
      to-port: 80
      cidr-block: 0.0.0.0/0
      rule-action: allow

    - rule-number: 110
      protocol: tcp
      from-port: 443
      to-port: 443
      cidr-block: 0.0.0.0/0
      rule-action: allow

    - rule-number: 120
      protocol: tcp
      from-port: 22
      to-port: 22
      cidr-block: 10.0.0.0/8
      rule-action: allow

    - rule-number: 130
      protocol: tcp
      from-port: 1024
      to-port: 65535
      cidr-block: 0.0.0.0/0
      rule-action: allow

    - rule-number: 999
      protocol: -1
      rule-action: deny

  egress:
    - rule-number: 100
      protocol: -1
      rule-action: allow

# Network ACL для Private Subnets
private-nacl:
  name: private-subnet-nacl

  ingress:
    - rule-number: 100
      protocol: tcp
      from-port: 80
      to-port: 80
      cidr-block: 10.0.0.0/16
      rule-action: allow

    - rule-number: 110
      protocol: tcp
      from-port: 443
      to-port: 443
      cidr-block: 10.0.0.0/16
      rule-action: allow

    - rule-number: 999
      protocol: -1
      rule-action: deny

  egress:
    - rule-number: 100
      protocol: tcp
      from-port: 80
      to-port: 80
      cidr-block: 0.0.0.0/0
      rule-action: allow

    - rule-number: 110
      protocol: tcp
      from-port: 443
      to-port: 443
      cidr-block: 0.0.0.0/0
      rule-action: allow

    - rule-number: 120
      protocol: tcp
      from-port: 3306
      to-port: 3306
      cidr-block: 10.0.20.0/24
      rule-action: allow
```

## Internet Gateway и NAT Gateway

### Internet Gateway
```yaml
# Internet Gateway Configuration
internet-gateway:
  name: production-igw
  vpc-id: vpc-12345678

  # Attach to VPC
  attachments:
    - vpc-id: vpc-12345678
      state: attached

# Route Table для Public Subnets
public-route-table:
  name: public-route-table
  vpc-id: vpc-12345678

  routes:
    - destination-cidr-block: 10.0.0.0/16
      target: local
      state: active

    - destination-cidr-block: 0.0.0.0/0
      target: igw-12345678
      state: active

  associations:
    - subnet-id: subnet-public-a
    - subnet-id: subnet-public-b
    - subnet-id: subnet-public-c
```

### NAT Gateway
```yaml
# NAT Gateway для Private Subnets
nat-gateway:
  name: production-nat-a
  subnet-id: subnet-public-a  # Must be in public subnet
  allocation-id: eipalloc-12345678
  connectivity-type: public

  # Route Table для Private Subnets
  private-route-table:
    name: private-route-table-a
    vpc-id: vpc-12345678

    routes:
      - destination-cidr-block: 10.0.0.0/16
        target: local

      - destination-cidr-block: 0.0.0.0/0
        target: nat-12345678

    associations:
      - subnet-id: subnet-private-app-a
      - subnet-id: subnet-private-data-a

# NAT Gateway с EIP
elastic-ip:
  name: nat-gateway-eip
  domain: vpc

nat-gateway-config:
  subnet-id: subnet-public-a
  allocation-id: eipalloc-12345678
  connectivity-type: public
  tags:
    - key: Name
      value: production-nat-gateway-a
    - key: Environment
      value: production
```

### Gateway Load Balancer (GWLB)
```yaml
# Gateway Load Balancer для Security Appliances
gateway-load-balancer:
  name: security-gwlb
  type: gateway
  subnets:
    - subnet-gwlb-a
    - subnet-gwlb-b

  target-group:
    name: firewall-targets
    protocol: GENEVE
    port: 6081
    target-type: instance
    targets:
      - id: i-firewall01
        port: 6081
      - id: i-firewall02
        port: 6081

  listener:
    protocol: GENEVE
    port: 6081
    default-actions:
      - type: forward
        target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/firewall-targets/12345678

# Route Table с GWLB endpoint
route-table-with-gwlb:
  routes:
    - destination-cidr-block: 0.0.0.0/0
      target: vpce-12345678  # GWLB endpoint
```

## VPC Peering и Transit Gateway

### VPC Peering
```yaml
# VPC Peering Connection
vpc-peering:
  name: dev-to-prod-peering
  vpc-id: vpc-dev-12345        # Requester VPC
  peer-vpc-id: vpc-prod-67890   # Accepter VPC
  peer-owner-id: 123456789012   # AWS Account ID
  peer-region: us-east-1

  # Accepter side configuration
  accepter:
    vpc-id: vpc-prod-67890
    peering-connection-id: pcx-12345678

# Route Tables для Peering
requester-routes:
  route-table-id: rtb-dev-private
  routes:
    - destination-cidr-block: 10.1.0.0/16  # Prod VPC CIDR
      target: pcx-12345678

accepter-routes:
  route-table-id: rtb-prod-private
  routes:
    - destination-cidr-block: 10.0.0.0/16  # Dev VPC CIDR
      target: pcx-12345678
```

### Transit Gateway
```yaml
# Transit Gateway
transit-gateway:
  name: organization-tgw
  description: Transit Gateway for multi-VPC connectivity
  amazon-side-asn: 64512
  auto-accept-shared-attachments: enable
  default-route-table-association: enable
  default-route-table-propagation: enable
  dns-support: enable
  vpn-ecmp-support: enable

  attachments:
    - name: vpc-dev-attachment
      vpc-id: vpc-dev-12345
      subnet-ids:
        - subnet-dev-tgw-a
        - subnet-dev-tgw-b

    - name: vpc-prod-attachment
      vpc-id: vpc-prod-67890
      subnet-ids:
        - subnet-prod-tgw-a
        - subnet-prod-tgw-b

    - name: vpn-attachment
      type: vpn
      vpn-connection-id: vpn-12345678

# Transit Gateway Route Table
tgw-route-table:
  name: production-routes
  transit-gateway-id: tgw-12345678

  routes:
    - destination-cidr-block: 10.0.0.0/16  # Dev VPC
      attachment-id: tgw-attach-dev
      state: active

    - destination-cidr-block: 0.0.0.0/0    # Default route via VPN
      attachment-id: tgw-attach-vpn
      state: active

  associations:
    - transit-gateway-attachment-id: tgw-attach-prod
      state: associated
```

### AWS Resource Access Manager (RAM)
```yaml
# Resource Share для Transit Gateway
resource-share:
  name: tgw-resource-share
  allow-external-principals: false
  principals:
    - "123456789012"  # AWS Account ID

  resources:
    - arn:aws:ec2:region:123456789012:transit-gateway/tgw-12345678

# Resource Share для VPC Subnets
subnet-share:
  name: subnet-resource-share
  allow-external-principals: true
  principals:
    - "arn:aws:organizations::123456789012:account/o-12345678"

  resources:
    - arn:aws:ec2:region:123456789012:subnet/subnet-12345
    - arn:aws:ec2:region:123456789012:subnet/subnet-67890
```

## Load Balancing

### Application Load Balancer (ALB)
```yaml
# Application Load Balancer
application-load-balancer:
  name: web-alb
  type: application
  scheme: internet-facing
  ip-address-type: ipv4
  subnets:
    - subnet-public-a
    - subnet-public-b
    - subnet-public-c

  security-groups:
    - sg-alb-12345

  listeners:
    - protocol: HTTP
      port: 80
      default-actions:
        - type: redirect
          redirect-config:
            protocol: HTTPS
            port: "443"
            status-code: HTTP_301

    - protocol: HTTPS
      port: 443
      ssl-policy: ELBSecurityPolicy-2016-08
      certificates:
        - certificate-arn: arn:aws:acm:region:account:certificate/cert-id
      default-actions:
        - type: forward
          target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/web-targets/12345678

  target-groups:
    - name: web-target-group
      protocol: HTTP
      port: 80
      vpc-id: vpc-12345678
      target-type: instance
      health-check:
        enabled: true
        interval: 30
        path: /health
        port: traffic-port
        protocol: HTTP
        timeout: 5
        healthy-threshold: 2
        unhealthy-threshold: 2
        matcher: 200

# ALB Listener Rules
listener-rules:
  - priority: 1
    conditions:
      - field: path-pattern
        values: ["/api/*"]
    actions:
      - type: forward
        target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/api-targets/87654321

  - priority: 2
    conditions:
      - field: host-header
        values: ["admin.myapp.com"]
    actions:
      - type: forward
        target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/admin-targets/11223344
```

### Network Load Balancer (NLB)
```yaml
# Network Load Balancer
network-load-balancer:
  name: api-nlb
  type: network
  scheme: internal
  ip-address-type: ipv4
  subnets:
    - subnet-private-a
    - subnet-private-b

  listeners:
    - protocol: TCP
      port: 80
      default-actions:
        - type: forward
          target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/api-tcp-80/12345678

    - protocol: TLS
      port: 443
      ssl-policy: ELBSecurityPolicy-2016-08
      certificates:
        - certificate-arn: arn:aws:acm:region:account:certificate/cert-id
      default-actions:
        - type: forward
          target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/api-tls-443/87654321

  target-groups:
    - name: api-tcp-targets
      protocol: TCP
      port: 8080
      vpc-id: vpc-12345678
      target-type: ip
      health-check:
        enabled: true
        interval: 30
        port: 8080
        protocol: TCP
        timeout: 10
        healthy-threshold: 2
        unhealthy-threshold: 2
```

### Load Balancer Best Practices
```yaml
# ALB Best Practices
alb-best-practices:
  security:
    - use-https-only: true
    - enable-deletion-protection: true
    - restrict-source-ips: false  # Use WAF instead

  performance:
    - enable-http2: true
    - enable-idle-timeout: 60
    - enable-cross-zone-load-balancing: true
    - use-connection-draining: true
    - draining-timeout: 300

  monitoring:
    - enable-access-logs: true
    - log-s3-bucket: alb-access-logs-bucket
    - log-s3-prefix: alb-logs/
    - enable-metrics: true

  high-availability:
    - minimum-availability-zones: 2
    - cross-zone-load-balancing: true
    - health-checks: comprehensive
    - auto-scaling-integration: true

# Target Group Best Practices
target-group-best-practices:
  health-checks:
    - protocol: HTTP
    - path: /health
    - interval: 30
    - timeout: 5
    - healthy-threshold: 2
    - unhealthy-threshold: 2
    - matcher: "200"

  attributes:
    - deregistration-delay: 30
    - stickiness-enabled: false
    - stickiness-type: lb_cookie
    - stickiness-duration: 86400
```

## Route 53 (DNS)

### Hosted Zones
```yaml
# Public Hosted Zone
public-hosted-zone:
  name: myapp.com
  description: Public hosted zone for myapp.com
  private-zone: false

  record-sets:
    - name: myapp.com
      type: A
      alias-target:
        dns-name: web-alb-123456789.us-east-1.elb.amazonaws.com
        hosted-zone-id: Z35SXDOTRQ7X7K
        evaluate-target-health: true

    - name: api.myapp.com
      type: A
      alias-target:
        dns-name: api-nlb-123456789.us-east-1.elb.amazonaws.com
        hosted-zone-id: Z31US982FNSXCF
        evaluate-target-health: true

    - name: www.myapp.com
      type: CNAME
      value: myapp.com

# Private Hosted Zone
private-hosted-zone:
  name: internal.myapp.com
  description: Private hosted zone for internal services
  private-zone: true
  vpc-associations:
    - vpc-id: vpc-12345678
    - vpc-id: vpc-87654321

  record-sets:
    - name: db.internal.myapp.com
      type: CNAME
      value: production-db.cluster-123456789.us-east-1.rds.amazonaws.com
      ttl: 300
```

### Routing Policies
```yaml
# Weighted Routing
weighted-routing:
  - name: web.myapp.com
    type: A
    set-identifier: primary
    weight: 100
    alias-target:
      dns-name: web-alb-primary.us-east-1.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K

  - name: web.myapp.com
    type: A
    set-identifier: secondary
    weight: 0  # Can be increased for failover
    alias-target:
      dns-name: web-alb-secondary.us-west-2.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K

# Latency-based Routing
latency-routing:
  - name: api.myapp.com
    type: A
    set-identifier: us-east-1
    region: us-east-1
    alias-target:
      dns-name: api-alb-east.us-east-1.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K

  - name: api.myapp.com
    type: A
    set-identifier: eu-west-1
    region: eu-west-1
    alias-target:
      dns-name: api-alb-eu.eu-west-1.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K

# Geo-location Routing
geo-routing:
  - name: content.myapp.com
    type: A
    set-identifier: us-geo
    geo-location:
      country-code: US
    alias-target:
      dns-name: us-cloudfront.cloudfront.net
      hosted-zone-id: Z2FDTNDATAQYW2

  - name: content.myapp.com
    type: A
    set-identifier: eu-geo
    geo-location:
      country-code: EU
    alias-target:
      dns-name: eu-cloudfront.cloudfront.net
      hosted-zone-id: Z2FDTNDATAQYW2
```

### Health Checks и Failover
```yaml
# Route 53 Health Checks
health-checks:
  - name: web-alb-health-check
    type: HTTP
    resource-path: /health
    fully-qualified-domain-name: web-alb-123456789.us-east-1.elb.amazonaws.com
    port: 80
    request-interval: 30
    failure-threshold: 3
    measure-latency: true
    regions:
      - us-east-1
      - us-west-1
      - eu-west-1

  - name: api-endpoint-health-check
    type: HTTPS
    resource-path: /api/health
    fully-qualified-domain-name: api.myapp.com
    port: 443
    request-interval: 30
    failure-threshold: 2
    enable-sni: true
    regions:
      - us-east-1

# Failover Routing
failover-routing:
  - name: app.myapp.com
    type: A
    set-identifier: primary
    failover: PRIMARY
    health-check-id: web-alb-health-check-id
    alias-target:
      dns-name: web-alb-primary.us-east-1.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K

  - name: app.myapp.com
    type: A
    set-identifier: secondary
    failover: SECONDARY
    alias-target:
      dns-name: web-alb-secondary.us-west-2.elb.amazonaws.com
      hosted-zone-id: Z35SXDOTRQ7X7K
```

## CloudFront (CDN)

### Distribution Configuration
```yaml
# CloudFront Distribution
cloudfront-distribution:
  name: web-distribution
  comment: CDN for web application
  enabled: true
  aliases:
    - www.myapp.com
    - cdn.myapp.com

  origins:
    - domain-name: web-alb-123456789.us-east-1.elb.amazonaws.com
      id: web-alb-origin
      custom-origin-config:
        http-port: 80
        https-port: 443
        origin-protocol-policy: https-only
        origin-ssl-protocols:
          - TLSv1.2

    - domain-name: api-nlb-123456789.us-east-1.elb.amazonaws.com
      id: api-nlb-origin
      custom-origin-config:
        http-port: 80
        https-port: 443
        origin-protocol-policy: https-only

    - domain-name: static-assets-bucket.s3.amazonaws.com
      id: s3-origin
      s3-origin-config:
        origin-access-identity: oai-12345678

  default-cache-behavior:
    target-origin-id: web-alb-origin
    viewer-protocol-policy: redirect-to-https
    allowed-methods: ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
    cached-methods: ["GET", "HEAD"]
    compress: true
    cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"  # CachingOptimized
    origin-request-policy-id: "216adef6-5c7f-47e4-b989-5492eafa07d3"  # AllViewer

  cache-behaviors:
    - path-pattern: "/api/*"
      target-origin-id: api-nlb-origin
      viewer-protocol-policy: https-only
      cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"
      origin-request-policy-id: "216adef6-5c7f-47e4-b989-5492eafa07d3"

    - path-pattern: "/static/*"
      target-origin-id: s3-origin
      viewer-protocol-policy: https-only
      cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"

  viewer-certificate:
    acm-certificate-arn: arn:aws:acm:us-east-1:account:certificate/cert-id
    ssl-support-method: sni-only
    minimum-protocol-version: TLSv1.2_2021

  restrictions:
    geo-restriction:
      restriction-type: whitelist
      locations:
        - US
        - CA
        - GB
        - DE
        - FR

  logging:
    enabled: true
    bucket: cloudfront-logs-bucket.s3.amazonaws.com
    prefix: cloudfront-logs/
    include-cookies: false
```

### Lambda@Edge
```javascript
// Lambda@Edge для URL rewriting
'use strict';

exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    const uri = request.uri;

    // Rewrite /app/* to /api/*
    if (uri.startsWith('/app/')) {
        request.uri = uri.replace('/app/', '/api/');
    }

    // Add custom headers
    request.headers['x-custom-header'] = [{
        key: 'X-Custom-Header',
        value: 'custom-value'
    }];

    callback(null, request);
};

// Lambda@Edge для authentication
exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    const headers = request.headers;

    const authHeader = headers.authorization;
    if (!authHeader) {
        callback(null, {
            status: '401',
            statusDescription: 'Unauthorized',
            headers: {
                'www-authenticate': [{ value: 'Bearer' }]
            }
        });
        return;
    }

    // Validate JWT token
    const token = authHeader[0].value.split(' ')[1];
    // Token validation logic...

    callback(null, request);
};
```

### CloudFront Functions
```javascript
// CloudFront Function для header manipulation
function handler(event) {
    var request = event.request;
    var headers = request.headers;

    // Add security headers
    headers['x-frame-options'] = { value: 'DENY' };
    headers['x-content-type-options'] = { value: 'nosniff' };
    headers['referrer-policy'] = { value: 'strict-origin-when-cross-origin' };

    // Remove sensitive headers
    delete headers['user-agent'];

    return request;
}

// CloudFront Function для URL redirects
function handler(event) {
    var request = event.request;
    var uri = request.uri;

    // Redirect old URLs
    if (uri === '/old-page') {
        return {
            statusCode: 301,
            statusDescription: 'Moved Permanently',
            headers: {
                'location': [{ value: '/new-page' }]
            }
        };
    }

    return request;
}
```

## Direct Connect и VPN

### Direct Connect
```yaml
# Direct Connect Connection
direct-connect-connection:
  name: office-to-aws
  bandwidth: 1Gbps
  location: EqDC2  # Equinix DC2 (Ashburn, VA)
  provider-name: AWS

# Direct Connect Gateway
direct-connect-gateway:
  name: organization-dxg
  amazon-side-asn: 64512

# Virtual Interface
private-virtual-interface:
  name: production-vif
  vlan: 100
  address-family: ipv4
  customer-address: 192.168.1.1/30
  amazon-address: 192.168.1.2/30
  customer-asn: 65000
  amazon-asn: 64512
  direct-connect-gateway-id: dxg-12345678

  bgp-peers:
    - address-family: ipv4
      customer-address: 192.168.1.1
      amazon-address: 192.168.1.2
      customer-asn: 65000
      amazon-asn: 64512
```

### Site-to-Site VPN
```yaml
# Customer Gateway
customer-gateway:
  name: office-cgw
  bgp-asn: 65000
  ip-address: 203.0.113.1  # Public IP of customer VPN device
  type: ipsec.1

# Virtual Private Gateway
virtual-private-gateway:
  name: production-vpg
  type: ipsec.1
  amazon-side-asn: 64512

# VPN Connection
vpn-connection:
  name: office-to-production
  type: ipsec.1
  customer-gateway-id: cgw-12345678
  virtual-private-gateway-id: vpg-12345678
  static-routes-only: false  # Use BGP

  tunnels:
    - tunnel-inside-cidr: 169.254.1.0/30
      pre-shared-key: my-pre-shared-key-1
    - tunnel-inside-cidr: 169.254.2.0/30
      pre-shared-key: my-pre-shared-key-2

# VPN Gateway Route Propagation
route-table:
  name: private-route-table
  routes:
    - destination-cidr-block: 192.168.0.0/16  # Office network
      target: vgw-12345678
  propagating-vgws:
    - gateway-id: vpg-12345678
```

### Client VPN
```yaml
# Client VPN Endpoint
client-vpn-endpoint:
  name: remote-access-vpn
  client-cidr-block: 10.100.0.0/16
  server-certificate-arn: arn:aws:acm:region:account:certificate/server-cert
  authentication-options:
    - type: certificate-authentication
      root-certificate-chain-arn: arn:aws:acm:region:account:certificate/client-cert
  connection-log-options:
    enabled: true
    cloudwatch-log-group: client-vpn-logs
    cloudwatch-log-stream: connections

# Client VPN Routes
client-vpn-routes:
  - destination-cidr-block: 10.0.0.0/16  # VPC CIDR
    target-vpc-subnet-id: subnet-vpn-a
    description: Route to production VPC

  - destination-cidr-block: 172.16.0.0/16  # Office network
    target-vpc-subnet-id: subnet-vpn-a
    description: Route to office network

# Authorization Rules
authorization-rules:
  - target-network-cidr: 10.0.0.0/16
    access-group-id: developers-group-id
    description: Allow developers access to production VPC

  - target-network-cidr: 172.16.0.0/16
    access-group-id: admin-group-id
    description: Allow admins access to office network
```

## Network Monitoring и Security

### VPC Reachability Analyzer
```yaml
# VPC Reachability Analysis
reachability-analysis:
  source:
    type: instance
    instance-id: i-source-instance
    network-interface-id: eni-source

  destination:
    type: instance
    instance-id: i-destination-instance
    network-interface-id: eni-destination

  protocol: tcp
  port: 80

# Analysis Result
analysis-result:
  reachability-status: reachable
  path:
    - sequence-number: 1
      component:
        id: eni-source
        type: aws:ec2:network-interface
    - sequence-number: 2
      component:
        id: subnet-source
        type: aws:ec2:subnet
    - sequence-number: 3
      component:
        id: rt-source
        type: aws:ec2:route-table
    - sequence-number: 4
      component:
        id: igw-main
        type: aws:ec2:internet-gateway
```

### Network Firewall
```yaml
# AWS Network Firewall
network-firewall:
  name: organization-firewall
  vpc-id: vpc-12345678
  subnet-mappings:
    - subnet-id: subnet-firewall-a
    - subnet-id: subnet-firewall-b

  firewall-policy:
    name: strict-policy
    stateless-default-actions:
      - action: aws:drop
    stateless-fragment-default-actions:
      - action: aws:drop
    stateful-default-actions:
      - action: aws:drop

    stateless-rule-groups:
      - priority: 1
        rule-group:
          rules-source:
            stateless-rules-and-custom-actions:
              rules:
                - rule-definition:
                    actions: ["aws:pass"]
                    match-attributes:
                      protocols: [6]  # TCP
                      source-ports:
                        - from-port: 0
                          to-port: 65535
                      destination-ports:
                        - from-port: 80
                          to-port: 80
                      tcp-flags:
                        - flags: ["SYN"]
                          masks: ["SYN"]
                  priority: 1

    stateful-rule-groups:
      - priority: 1
        rule-group:
          rules-source:
            rules-source-list:
              targets: ["example.com"]
              target-types: ["HTTP_HOST"]
              generated-rules-type: "ALLOWLIST"
```

### Traffic Mirroring
```yaml
# VPC Traffic Mirroring
traffic-mirror-session:
  name: security-monitoring
  network-interface-id: eni-web-server
  traffic-mirror-target-id: tmt-12345678
  traffic-mirror-filter-id: tmf-12345678
  session-number: 1
  packet-length: 96
  virtual-network-id: 123456

# Traffic Mirror Target (NLB)
traffic-mirror-target:
  name: security-analysis-target
  type: network-load-balancer
  network-load-balancer-arn: arn:aws:elasticloadbalancing:region:account:loadbalancer/net/security-nlb/12345678

# Traffic Mirror Filter
traffic-mirror-filter:
  name: security-filter
  network-services:
    - amazon-dns

  inbound-rules:
    - rule-number: 1
      rule-action: accept
      traffic-direction: ingress
      protocol: 6  # TCP
      destination-cidr-block: 0.0.0.0/0
      source-port-range:
        from-port: 0
        to-port: 65535
      destination-port-range:
        from-port: 22
        to-port: 22

  outbound-rules:
    - rule-number: 1
      rule-action: accept
      traffic-direction: egress
      protocol: 6  # TCP
      destination-cidr-block: 0.0.0.0/0
      source-port-range:
        from-port: 22
        to-port: 22
      destination-port-range:
        from-port: 0
        to-port: 65535
```

### Network ACL Monitoring
```yaml
# CloudWatch Alarms для Network ACLs
network-acl-alarms:
  - alarm-name: NACL-RejectedPackets
    alarm-description: "High number of rejected packets"
    metric-name: RejectedPackets
    namespace: AWS/EC2
    dimensions:
      - name: NetworkAcl
        value: acl-12345678
    statistic: Sum
    period: 300
    threshold: 1000
    comparison-operator: GreaterThanThreshold
    evaluation-periods: 2

  - alarm-name: NACL-AllowedPackets
    alarm-description: "Low number of allowed packets"
    metric-name: AllowedPackets
    namespace: AWS/EC2
    dimensions:
      - name: NetworkAcl
        value: acl-12345678
    statistic: Sum
    period: 300
    threshold: 100
    comparison-operator: LessThanThreshold
    evaluation-periods: 3
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [AWS Basics](aws-basics.md) — основы **AWS**
- [AWS Services](aws-services.md) — сервисы **AWS**
- [AWS IAM](aws-iam.md) — управление доступом
- [Terraform](../iac/terraform/terraform-basics.md) — **IaC** инструмент