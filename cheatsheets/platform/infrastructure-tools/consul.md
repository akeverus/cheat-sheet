---
title: "Consul"
description: "Consul - это распределенная система для service discovery, configuration management и service mesh от HashiCorp. Он обеспечивает надежное обнаружение сервисов, распределенную конфигурацию, health checking и service-to-service communication с автоматическим шифрованием. Этот докум"
tags: ["platform", "infrastructure-tools", "consul"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Consul**

**Consul** - это распределенная система для **service discovery**, **configuration management** и **service mesh** от **HashiCorp**. Он обеспечивает надежное обнаружение сервисов, распределенную конфигурацию, **health checking** и **service-to-service communication** с автоматическим шифрованием. Этот документ охватывает **enterprise-grade** паттерны использования **Consul** в **production** средах.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки
- [Consul Documentation](https://developer.hashicorp.com/consul/docs)
- [Consul Connect](https://developer.hashicorp.com/consul/docs/connect)
- [Consul ACL](https://developer.hashicorp.com/consul/docs/security/acl)
- [Consul Service Mesh](https://developer.hashicorp.com/consul/docs/connect)
- [Consul GitHub](https://github.com/hashicorp/consul)
- [Consul Enterprise](https://www.hashicorp.com/products/consul)

## Содержание

- [Основы Consul](#основы-consul)
  - [Установка и настройка](#установка-и-настройка)
  - [Базовая архитектура](#базовая-архитектура)
  - [Запуск кластера](#запуск-кластера)
- [Service Discovery](#service-discovery)
  - [Регистрация сервисов](#регистрация-сервисов)
  - [HTTP API для service discovery](#http-api-для-service-discovery)
  - [DNS interface](#dns-interface)
- [Key-Value Store](#key-value-store)
  - [KV operations](#kv-operations)
  - [Configuration management](#configuration-management)
  - [Consul Template](#consul-template)
- [Service Mesh (Consul Connect)](#service-mesh-consul-connect)
  - [Sidecar proxy](#sidecar-proxy)
  - [Intentions](#intentions)
  - [Service-to-service communication](#service-to-service-communication)
- [ACL (Access Control Lists)](#acl-access-control-lists)
  - [Bootstrap ACL](#bootstrap-acl)
  - [Policy definitions](#policy-definitions)
  - [Token management](#token-management)
- [Health Checks](#health-checks)
  - [Различные типы health checks](#различные-типы-health-checks)
  - [Distributed health checks](#distributed-health-checks)
- [Federation и Multi-datacenter](#federation-и-multi-datacenter)
  - [WAN federation](#wan-federation)
  - [Cross-datacenter communication](#cross-datacenter-communication)
- [Monitoring и Observability](#monitoring-и-observability)
  - [Metrics collection](#metrics-collection)
  - [Prometheus integration](#prometheus-integration)
  - [Alerting rules](#alerting-rules)
- [Enterprise Security](#enterprise-security)
  - [TLS encryption](#tls-encryption)
  - [Audit logging](#audit-logging)
  - [Secrets management integration](#secrets-management-integration)
- [Performance Tuning](#performance-tuning)
  - [Server optimization](#server-optimization)
  - [Client optimization](#client-optimization)
- [Disaster Recovery](#disaster-recovery)
  - [Backup strategy](#backup-strategy)
  - [Recovery procedures](#recovery-procedures)
- [Решение проблем](#решение-проблем)
  - [Common issues](#common-issues)
  - [Performance issues](#performance-issues)
  - [Network issues](#network-issues)
- [Лучшие практики](#лучшие-практики)
  - [Production deployment](#production-deployment)
  - [Monitoring dashboard](#monitoring-dashboard)
  - [Security checklist](#security-checklist)
- [См. также](#см-также)

## Основы **Consul**

### Установка и настройка
Ниже — установка **Consul** на **Linux**/**macOS** (**bash**).
```bash
# Linux/macOS установка
curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
sudo apt-get update && sudo apt-get install consul

# macOS с Homebrew
brew tap hashicorp/tap
brew install hashicorp/tap/consul

# Docker
docker run -d --name consul \
  -p 8500:8500 \
  -p 8600:8600/udp \
  consul:latest agent -server -bootstrap -ui -client=0.0.0.0

# Проверка установки
consul version
consul --help
```

### Базовая архитектура
```json
// consul-config.json
{
  "datacenter": "dc1",
  "data_dir": "/opt/consul/data",
  "log_level": "INFO",
  "server": true,
  "bootstrap_expect": 3,
  "ui": true,
  "client_addr": "0.0.0.0",
  "bind_addr": "{{GetInterfaceIP \"eth0\"}}",
  "advertise_addr": "{{GetInterfaceIP \"eth0\"}}",
  "retry_join": [
    "10.0.1.10",
    "10.0.1.11",
    "10.0.1.12"
  ],
  "encrypt": "YOUR_GOSSIP_ENCRYPTION_KEY",
  "ca_file": "/etc/consul/ssl/ca.pem",
  "cert_file": "/etc/consul/ssl/consul.pem",
  "key_file": "/etc/consul/ssl/consul-key.pem",
  "verify_incoming": true,
  "verify_outgoing": true,
  "verify_server_hostname": true,
  "ports": {
    "http": 8500,
    "https": 8501,
    "grpc": 8502,
    "dns": 8600,
    "serf_lan": 8301,
    "serf_wan": 8302,
    "server": 8300
  },
  "acl": {
    "enabled": true,
    "default_policy": "deny",
    "enable_token_persistence": true
  },
  "connect": {
    "enabled": true
  }
}
```

### Запуск кластера
```bash
# Server 1
consul agent -server -bootstrap-expect=3 -data-dir=/tmp/consul1 \
  -node=consul1 -bind=127.0.0.1 -client=127.0.0.1 \
  -ui -config-dir=/etc/consul.d

# Server 2
consul agent -server -bootstrap-expect=3 -data-dir=/tmp/consul2 \
  -node=consul2 -bind=127.0.0.2 -client=127.0.0.1 \
  -retry-join=127.0.0.1 -config-dir=/etc/consul.d

# Server 3
consul agent -server -bootstrap-expect=3 -data-dir=/tmp/consul3 \
  -node=consul3 -bind=127.0.0.3 -client=127.0.0.1 \
  -retry-join=127.0.0.1 -config-dir=/etc/consul.d

# Client
consul agent -data-dir=/tmp/consul-client -node=client1 \
  -bind=127.0.0.4 -client=127.0.0.1 \
  -retry-join=127.0.0.1 -config-dir=/etc/consul.d
```

## Service Discovery

### Регистрация сервисов
```json
// web-service.json
{
  "service": {
    "name": "web",
    "tags": ["rails", "production"],
    "address": "10.0.1.100",
    "port": 8080,
    "check": {
      "id": "web-health",
      "name": "Web Health Check",
      "http": "http://10.0.1.100:8080/health",
      "method": "GET",
      "interval": "10s",
      "timeout": "1s",
      "deregister_critical_service_after": "30s"
    },
    "weights": {
      "passing": 10,
      "warning": 1
    }
  }
}
```

### HTTP API для **service discovery**
```bash
# Регистрация сервиса
curl -X PUT \
  http://localhost:8500/v1/agent/service/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "api",
    "address": "10.0.1.101",
    "port": 9090,
    "tags": ["api", "v1"],
    "check": {
      "http": "http://10.0.1.101:9090/health",
      "interval": "30s",
      "timeout": "5s"
    }
  }'

# Поиск сервисов
curl http://localhost:8500/v1/catalog/services

# Получение инстансов сервиса
curl http://localhost:8500/v1/catalog/service/web

# Health checks
curl http://localhost:8500/v1/health/service/web

# DNS lookup
dig @127.0.0.1 -p 8600 web.service.consul

# Service discovery с фильтрами
curl "http://localhost:8500/v1/health/service/api?passing=true&tag=v1"
```

### DNS interface
```bash
# Standard DNS queries
dig @127.0.0.1 -p 8600 web.service.consul
dig @127.0.0.1 -p 8600 api.service.consul SRV

# Prepared queries
curl -X POST \
  http://localhost:8500/v1/query \
  -d '{
    "name": "web-prod",
    "service": {
      "service": "web",
      "tags": ["production"],
      "only_passing": true
    }
  }'

# Использование prepared query
dig @127.0.0.1 -p 8600 web-prod.query.consul
```

## Key-Value Store

### `KV` **operations**
```bash
# Установка значения
curl -X PUT \
  http://localhost:8500/v1/kv/config/database/url \
  -d "postgresql://user:pass@db:5432/myapp"

# Получение значения
curl http://localhost:8500/v1/kv/config/database/url

# Получение с метаданными
curl http://localhost:8500/v1/kv/config/database/url?raw=false

# Рекурсивное получение
curl http://localhost:8500/v1/kv/config?recurse=true

# Удаление ключа
curl -X DELETE http://localhost:8500/v1/kv/config/database/url

# Watch за изменениями
curl "http://localhost:8500/v1/kv/config/database/url?wait=5s&index=123"
```

### Configuration management
```json
// config-template.json
{
  "template": {
    "source": "/etc/consul-templates/app.conf.ctmpl",
    "destination": "/etc/myapp/app.conf",
    "command": "systemctl reload myapp",
    "command_timeout": "60s",
    "perms": "0644",
    "backup": true,
    "wait": {
      "min": "2s",
      "max": "10s"
    }
  }
}
```

### Consul Template
```hcl
# app.conf.ctmpl
upstream backend {
{{range service "api" "passing"}}
  server {{.Address}}:{{.Port}} weight={{.Weights.Passing}};
{{end}}
}

server {
  listen 80;
  server_name {{key "config/domain"}};

  location / {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }

  # Database configuration
  location /api/ {
    {{with secret "database/creds/myapp"}}
    proxy_set_header X-DB-URL "{{.Data.url}}";
    {{end}}
  }
}
```

## Service Mesh (**Consul Connect**)

### Sidecar proxy
```json
// service-with-connect.json
{
  "service": {
    "name": "web",
    "port": 8080,
    "connect": {
      "sidecar_service": {
        "proxy": {
          "upstreams": [
            {
              "destination_name": "api",
              "local_bind_port": 9090
            }
          ]
        }
      }
    }
  }
}
```

### Intentions
```bash
# Создание intention
curl -X PUT \
  http://localhost:8500/v1/connect/intentions \
  -d '{
    "SourceName": "web",
    "DestinationName": "api",
    "Action": "allow"
  }'

# Просмотр intentions
curl http://localhost:8500/v1/connect/intentions

# Проверка разрешения
curl "http://localhost:8500/v1/connect/intentions/check?source=web&destination=api"
```

### Service-to-service communication
```javascript
// Node.js клиент с Connect
const consul = require('consul');
const http = require('http');

const client = consul();

async function callAPI() {
  // Получение upstream через Consul DNS
  const upstream = 'api.service.consul:9090';

  return new Promise((resolve, reject) => {
    const req = http.request({
      hostname: upstream.split(':')[0],
      port: upstream.split(':')[1],
      path: '/api/data',
      method: 'GET'
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => resolve(JSON.parse(data)));
    });

    req.on('error', reject);
    req.end();
  });
}
```

## ACL (`Access Control Lists`)

### Bootstrap ACL
```bash
# Инициализация ACL
consul acl bootstrap

# Создание токена управления
curl -X PUT \
  http://localhost:8500/v1/acl/token \
  -H "X-Consul-Token: $MASTER_TOKEN" \
  -d '{
    "name": "admin-token",
    "type": "management",
    "rules": ""
  }'
```

### Policy definitions
```hcl
# web-policy.hcl
service "web" {
  policy = "write"
}

service "api" {
  policy = "read"
}

key_prefix "config/web/" {
  policy = "write"
}

key_prefix "config/shared/" {
  policy = "read"
}

agent_prefix "" {
  policy = "read"
}

event_prefix "" {
  policy = "write"
}
```

### Token management
```bash
# Создание policy
curl -X PUT \
  http://localhost:8500/v1/acl/policy \
  -H "X-Consul-Token: $MASTER_TOKEN" \
  -d '{
    "name": "web-policy",
    "rules": "service \"web\" { policy = \"write\" }"
  }'

# Создание токена
curl -X PUT \
  http://localhost:8500/v1/acl/token \
  -H "X-Consul-Token: $MASTER_TOKEN" \
  -d '{
    "name": "web-token",
    "policies": [
      {"name": "web-policy"}
    ]
  }'

# Использование токена
curl -H "X-Consul-Token: $WEB_TOKEN" \
  http://localhost:8500/v1/health/service/web
```

## Health Checks

### Различные типы **health checks**
```json
// comprehensive-health-checks.json
{
  "services": [
    {
      "name": "web",
      "checks": [
        {
          "id": "web-tcp",
          "name": "Web TCP Check",
          "tcp": "localhost:8080",
          "interval": "10s",
          "timeout": "1s"
        },
        {
          "id": "web-http",
          "name": "Web HTTP Check",
          "http": "http://localhost:8080/health",
          "method": "GET",
          "header": {
            "Authorization": ["Bearer ${WEB_TOKEN}"]
          },
          "interval": "30s",
          "timeout": "5s"
        },
        {
          "id": "web-script",
          "name": "Web Script Check",
          "args": ["/usr/local/bin/check_web.sh"],
          "interval": "60s",
          "timeout": "10s"
        }
      ]
    }
  ]
}
```

### Distributed health checks
```bash
# Watch за health status
curl "http://localhost:8500/v1/health/state/critical?wait=30s&index=123"

# Service health с фильтрами
curl "http://localhost:8500/v1/health/service/web?passing=true&near=client1"

# Node health
curl "http://localhost:8500/v1/health/node/consul1"

# Custom health check script
#!/bin/bash
# check_web.sh

# Проверка HTTP статуса
status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health)

if [ "$status" -eq 200 ]; then
  echo "Web service is healthy"
  exit 0
else
  echo "Web service is unhealthy: HTTP $status"
  exit 1
fi
```

## Federation и **Multi-datacenter**

### WAN federation
```json
// wan-config.json
{
  "datacenter": "dc1",
  "primary_datacenter": "dc1",
  "acl": {
    "enabled": true,
    "default_policy": "deny"
  },
  "connect": {
    "enable_mesh_gateway_wan_federation": true
  },
  "ports": {
    "serf_wan": 8302
  }
}
```

### Cross-datacenter communication
```bash
# Настройка mesh gateway
curl -X PUT \
  http://localhost:8500/v1/config \
  -d '{
    "kind": "mesh-gateway",
    "name": "mesh-gateway",
    "config": {
      "listeners": [
        {
          "protocol": "tcp",
          "port": 443
        }
      ]
    }
  }'

# Настройка peering
curl -X POST \
  http://localhost:8500/v1/peering/token \
  -d '{
    "PeerName": "dc2",
    "ServerExternalAddresses": ["consul-dc2.example.com:8300"]
  }'

# Использование peering
curl "http://localhost:8500/v1/health/connect/web?peer=dc2"
```

## Monitoring и **Observability**

### Metrics collection
```json
// telemetry-config.json
{
  "telemetry": {
    "disable_hostname": false,
    "prometheus_retention_time": "60s",
    "statsd_address": "127.0.0.1:8125",
    "statsite_address": "127.0.0.1:8125",
    "dogstatsd_addr": "127.0.0.1:8125",
    "dogstatsd_tags": ["env:production"],
    "circonus_api_token": "YOUR_CIRCONUS_TOKEN",
    "circonus_api_app": "consul",
    "circonus_api_url": "https://api.circonus.com/v2",
    "circonus_submission_interval": "10s",
    "circonus_check_id": "YOUR_CHECK_ID",
    "circonus_check_force_metric_activation": true,
    "circonus_check_instance_id": "consul:prod",
    "circonus_check_search_tag": "service:consul",
    "circonus_check_tags": "service:consul",
    "circonus_broker_id": "YOUR_BROKER_ID",
    "circonus_broker_select_tag": "dc:dc1"
  }
}
```

### Prometheus integration
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'consul-server'
    static_configs:
      - targets: ['consul1:8500', 'consul2:8500', 'consul3:8500']
    metrics_path: '/v1/agent/metrics'
    params:
      format: ['prometheus']

  - job_name: 'consul-services'
    consul_sd_configs:
      - server: 'localhost:8500'
        services: ['web', 'api', 'db']
    relabel_configs:
      - source_labels: ['__meta_consul_service']
        target_label: 'service'
      - source_labels: ['__meta_consul_node']
        target_label: 'node'
```

### Alerting rules
```yaml
# alert-rules.yml
groups:
  - name: consul
    rules:
      - alert: ConsulServiceUnhealthy
        expr: consul_health_service_status{status="critical"} > 0
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Consul service is unhealthy"
          description: "Service {{ $labels.service }} on {{ $labels.node }} is unhealthy"

      - alert: ConsulLeaderless
        expr: consul_raft_leader == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Consul cluster has no leader"
          description: "Consul cluster in DC {{ $labels.datacenter }} has no leader"

      - alert: ConsulPeersDown
        expr: consul_serf_lan_members_total{status="alive"} < 3
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Consul peers are down"
          description: "Only {{ $value }} Consul peers are alive"
```

## Enterprise Security

### TLS encryption
```bash
# Генерация сертификатов
# Создание CA
consul tls ca create

# Создание server certificates
consul tls cert create -server -dc dc1

# Создание client certificates
consul tls cert create -client -dc dc1

# Конфигурация TLS в Consul
{
  "ca_file": "/etc/consul/ssl/ca.pem",
  "cert_file": "/etc/consul/ssl/consul.pem",
  "key_file": "/etc/consul/ssl/consul-key.pem",
  "verify_incoming": true,
  "verify_outgoing": true,
  "verify_server_hostname": true,
  "ports": {
    "https": 8501
  }
}
```

### Audit logging
```json
// audit-config.json
{
  "audit": {
    "enabled": true,
    "sink": {
      "type": "file",
      "format": "json",
      "path": "/var/log/consul/audit.log",
      "rotate_duration": "24h",
      "rotate_max_files": 7
    },
    "event_types": ["*"]
  }
}
```

### Secrets management integration
```bash
# Vault integration
curl -X PUT \
  http://localhost:8500/v1/config \
  -d '{
    "kind": "secret-backend",
    "name": "vault",
    "config": {
      "address": "https://vault.example.com:8200",
      "token": "VAULT_TOKEN",
      "path": "secret/"
    }
  }'

# Использование секретов
curl -H "X-Consul-Token: $TOKEN" \
  http://localhost:8500/v1/secrets/database/creds/web
```

## Performance Tuning

### Server optimization
```json
// performance-config.json
{
  "performance": {
    "raft_multiplier": 1,
    "rpc_rate_limit": 100.0,
    "rpc_max_burst": 1000,
    "rpc_hold_timeout": "7s"
  },
  "limits": {
    "http_max_conns_per_client": 200,
    "https_handshake_timeout": "10s",
    "http_max_req_header_bytes": 4096,
    "http_max_req_timeout": "30s",
    "http_req_timeout": "30s",
    "http_max_resp_header_bytes": 4096
  },
  "dns_config": {
    "allow_stale": true,
    "max_stale": "87600h",
    "node_ttl": "30s",
    "service_ttl": {
      "*": "30s"
    },
    "enable_truncate": true,
    "only_passing": true,
    "recursor_timeout": "2s"
  }
}
```

### Client optimization
```json
// client-config.json
{
  "leave_on_terminate": true,
  "skip_leave_on_interrupt": false,
  "disable_remote_exec": true,
  "disable_update_check": true,
  "enable_script_checks": true,
  "enable_local_script_checks": true,
  "enable_syslog": true,
  "syslog_facility": "LOCAL5",
  "reconnect_timeout": "15s",
  "reconnect_timeout_wan": "30s"
}
```

## Disaster Recovery

### Backup strategy
```bash
# Snapshot создание
consul snapshot save backup.snap

# Snapshot восстановление
consul snapshot restore backup.snap

# Автоматизированный backup
#!/bin/bash
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="consul_backup_$TIMESTAMP.snap"

consul snapshot save "$BACKUP_FILE"

# Upload to S3
aws s3 cp "$BACKUP_FILE" "s3://consul-backups/$BACKUP_FILE"

# Очистка старых бэкапов (оставить последние 7)
aws s3 ls s3://consul-backups/ | sort -r | tail -n +8 | awk '{print $4}' | xargs -I {} aws s3 rm s3://consul-backups/{}
```

### Recovery procedures
```bash
# Emergency leader election
consul operator raft transfer-leader

# Force leave unhealthy node
consul operator raft remove-peer -address=10.0.1.10:8300

# Restore from backup
consul snapshot restore backup.snap

# Rejoin cluster
consul agent -retry-join=10.0.1.11 -retry-join=10.0.1.12

# Validate cluster health
consul operator raft list-peers
consul members
```

## Решение проблем

### Common issues
```bash
# Проверка статуса кластера
consul members
consul operator raft list-peers

# Логи агента
tail -f /var/log/consul/consul.log

# Debug информация
consul operator raft state
consul operator autopilot state

# Проверка health checks
curl http://localhost:8500/v1/health/state/critical

# DNS troubleshooting
dig @127.0.0.1 -p 8600 web.service.consul

# Network connectivity
telnet 127.0.0.1 8301
```

### Performance issues
```bash
# Metrics collection
curl http://localhost:8500/v1/agent/metrics

# Slow queries debugging
curl "http://localhost:8500/v1/kv/config?recurse=true&keys=true"

# Memory usage
ps aux | grep consul
top -p $(pgrep consul)

# Disk usage
du -sh /opt/consul/data
df -h
```

### Network issues
```bash
# Firewall rules
iptables -L -n
ufw status

# Port availability
netstat -tlnp | grep :830
ss -tlnp | grep :830

# DNS resolution
nslookup consul.service.consul
dig @127.0.0.1 -p 8600 consul.service.consul SRV
```

## Лучшие практики

### Production deployment
```json
// production-config.json
{
  "datacenter": "production",
  "server": true,
  "bootstrap_expect": 5,
  "ui": false,
  "client_addr": "127.0.0.1",
  "bind_addr": "{{GetInterfaceIP \"eth0\"}}",
  "advertise_addr": "{{GetInterfaceIP \"eth0\"}}",
  "retry_join": [
    "provider=aws tag_key=consul-server tag_value=true"
  ],
  "encrypt": "{{key \"gossip/encrypt\"}}",
  "ca_file": "/etc/consul/ssl/ca.pem",
  "cert_file": "/etc/consul/ssl/consul.pem",
  "key_file": "/etc/consul/ssl/consul-key.pem",
  "verify_incoming": true,
  "verify_outgoing": true,
  "verify_server_hostname": true,
  "acl": {
    "enabled": true,
    "default_policy": "deny",
    "enable_token_persistence": true,
    "down_policy": "extend-cache"
  },
  "connect": {
    "enabled": true,
    "ca_provider": "vault",
    "ca_config": {
      "address": "https://vault.service.consul:8200",
      "token": "{{key \"vault/token\"}}",
      "root_pki_path": "connect_root",
      "intermediate_pki_path": "connect_inter"
    }
  },
  "telemetry": {
    "prometheus_retention_time": "60s",
    "disable_hostname": true
  },
  "performance": {
    "raft_multiplier": 1
  }
}
```

### Monitoring dashboard
```json
// grafana-dashboard.json
{
  "dashboard": {
    "title": "Consul Cluster Health",
    "panels": [
      {
        "title": "Cluster Members",
        "type": "stat",
        "targets": [
          {
            "expr": "consul_serf_lan_members_total",
            "legendFormat": "{{status}}"
          }
        ]
      },
      {
        "title": "Service Health",
        "type": "table",
        "targets": [
          {
            "expr": "consul_health_service_status",
            "legendFormat": "{{service}} - {{status}}"
          }
        ]
      },
      {
        "title": "Raft State",
        "type": "stat",
        "targets": [
          {
            "expr": "consul_raft_state",
            "legendFormat": "{{state}}"
          }
        ]
      }
    ]
  }
}
```

### Security checklist
```bash
# Pre-deployment security checklist
#!/bin/bash

echo "Consul Security Checklist"
echo "========================"

# ACL enabled
if curl -s http://localhost:8500/v1/acl/policies | grep -q "default"; then
  echo "✅ ACL enabled"
else
  echo "❌ ACL not enabled"
fi

# TLS enabled
if curl -s https://localhost:8501/v1/status/leader >/dev/null 2>&1; then
  echo "✅ TLS enabled"
else
  echo "❌ TLS not enabled"
fi

# Gossip encryption
if consul members | grep -q "alive"; then
  echo "✅ Gossip encryption enabled"
else
  echo "❌ Gossip encryption not configured"
fi

# Connect enabled
if curl -s http://localhost:8500/v1/connect/ca/roots | jq -r '.Roots[0].Name' >/dev/null 2>&1; then
  echo "✅ Service mesh enabled"
else
  echo "❌ Service mesh not enabled"
fi

echo "Checklist complete"
```
## См. также
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
- [Docker](../containers/docker/docker-basics.md) — контейнеризация
- [Kubernetes](../containers/kubernetes/kubernetes-basics.md) — оркестрация контейнеров
