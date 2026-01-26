# Grafana

Grafana - это платформа для аналитики и интерактивной визуализации данных с открытым исходным кодом. Grafana позволяет создавать дашборды и графики на основе данных из различных источников, включая Prometheus, Elasticsearch, InfluxDB и многие другие.

## Содержание
- [Основы Grafana](#основы-grafana)
- [Data Sources](#data-sources)
- [Создание дашбордов](#создание-дашбордов)
- [Типы панелей](#типы-панелей)
- [Alerting](#alerting)
- [Плагины и расширения](#плагины-и-расширения)
- [Управление пользователями и доступом](#управление-пользователями-и-доступом)
- [Provisioning](#provisioning)
- [API и автоматизация](#api-и-автоматизация)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)

## Основы Grafana

### Архитектура Grafana
```yaml
# Основные компоненты:
# - Grafana Server: веб-сервер и API
# - Database: хранение конфигурации (SQLite, PostgreSQL, MySQL)
# - Data Sources: источники данных (Prometheus, Elasticsearch, etc.)
# - Dashboards: дашборды с панелями
# - Plugins: расширения функциональности
# - Users & Teams: управление доступом
```

### Установка и запуск

#### Docker
```bash
# Запуск Grafana в Docker
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  -v grafana-data:/var/lib/grafana \
  grafana/grafana

# С внешней базой данных
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_DATABASE_TYPE=postgres \
  -e GF_DATABASE_HOST=postgres:5432 \
  -e GF_DATABASE_NAME=grafana \
  -e GF_DATABASE_USER=grafana \
  -e GF_DATABASE_PASSWORD=password \
  -v grafana-data:/var/lib/grafana \
  grafana/grafana
```

#### Kubernetes
```yaml
# grafana-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:latest
        ports:
        - containerPort: 3000
        env:
        - name: GF_SECURITY_ADMIN_PASSWORD
          value: "admin"
        - name: GF_DATABASE_TYPE
          value: "postgres"
        - name: GF_DATABASE_HOST
          value: "postgres-service"
        volumeMounts:
        - name: grafana-storage
          mountPath: /var/lib/grafana
      volumes:
      - name: grafana-storage
        persistentVolumeClaim:
          claimName: grafana-pvc

---
apiVersion: v1
kind: Service
metadata:
  name: grafana
  namespace: monitoring
spec:
  selector:
    app: grafana
  ports:
  - port: 3000
    targetPort: 3000
  type: ClusterIP
```

#### Конфигурация
```ini
# /etc/grafana/grafana.ini или GF_ переменные окружения

[server]
http_addr = 0.0.0.0
http_port = 3000
domain = grafana.example.com

[database]
type = postgres
host = postgres:5432
name = grafana
user = grafana
password = password

[security]
admin_user = admin
admin_password = admin
secret_key = your-secret-key

[auth]
disable_login_form = false
oauth_auto_login = false

[auth.anonymous]
enabled = true
org_role = Viewer

[alerting]
enabled = true

[metrics]
enabled = true
```

## Data Sources

### Prometheus
```json
{
  "name": "Prometheus",
  "type": "prometheus",
  "url": "http://prometheus:9090",
  "access": "proxy",
  "isDefault": true,
  "jsonData": {
    "timeInterval": "15s",
    "queryTimeout": "60s",
    "httpMethod": "POST"
  },
  "secureJsonData": {
    "password": "your-password"
  }
}
```

### Elasticsearch
```json
{
  "name": "Elasticsearch",
  "type": "elasticsearch",
  "url": "http://elasticsearch:9200",
  "access": "proxy",
  "database": "[logstash-]YYYY.MM.DD",
  "jsonData": {
    "esVersion": "7.0.0",
    "timeField": "@timestamp",
    "interval": "Daily",
    "logMessageField": "message",
    "logLevelField": "level"
  }
}
```

### InfluxDB
```json
{
  "name": "InfluxDB",
  "type": "influxdb",
  "url": "http://influxdb:8086",
  "access": "proxy",
  "database": "metrics",
  "jsonData": {
    "httpMode": "POST",
    "version": "Flux"
  },
  "secureJsonData": {
    "token": "your-influx-token"
  }
}
```

### PostgreSQL
```json
{
  "name": "PostgreSQL",
  "type": "postgres",
  "url": "postgres:5432",
  "access": "proxy",
  "database": "metrics",
  "user": "grafana",
  "jsonData": {
    "sslmode": "disable",
    "maxOpenConns": 100,
    "maxIdleConns": 100,
    "connMaxLifetime": 14400
  },
  "secureJsonData": {
    "password": "your-password"
  }
}
```

## Создание дашбордов

### Структура дашборда
```json
{
  "dashboard": {
    "title": "System Monitoring",
    "tags": ["system", "monitoring"],
    "timezone": "browser",
    "refresh": "30s",
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "panels": [],
    "templating": {
      "list": []
    }
  }
}
```

### Переменные шаблонов
```json
{
  "templating": {
    "list": [
      {
        "name": "datasource",
        "type": "datasource",
        "query": "prometheus",
        "label": "Data Source"
      },
      {
        "name": "instance",
        "type": "query",
        "datasource": "$datasource",
        "query": "label_values(up, instance)",
        "label": "Instance",
        "multi": true,
        "includeAll": true
      },
      {
        "name": "job",
        "type": "query",
        "datasource": "$datasource",
        "query": "label_values(up, job)",
        "label": "Job"
      }
    ]
  }
}
```

## Типы панелей

### Graph Panel
```json
{
  "title": "CPU Usage",
  "type": "graph",
  "targets": [
    {
      "expr": "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\", instance=\"$instance\"}[5m])) * 100)",
      "legendFormat": "{{instance}}",
      "refId": "A"
    }
  ],
  "yAxes": [
    {
      "unit": "percent",
      "min": 0,
      "max": 100
    },
    {
      "unit": "short"
    }
  ],
  "seriesOverrides": [
    {
      "alias": "/.*Total.*/",
      "fill": 0
    }
  ]
}
```

### Table Panel
```json
{
  "title": "Top CPU Processes",
  "type": "table",
  "targets": [
    {
      "expr": "topk(10, rate(process_cpu_seconds_total[5m]))",
      "legendFormat": "{{pid}} - {{comm}}",
      "refId": "A"
    }
  ],
  "transform": "table",
  "columns": [
    {
      "text": "Process",
      "value": "comm"
    },
    {
      "text": "PID",
      "value": "pid"
    },
    {
      "text": "CPU %",
      "value": "value"
    }
  ]
}
```

### Gauge Panel
```json
{
  "title": "Memory Usage",
  "type": "gauge",
  "targets": [
    {
      "expr": "(1 - node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100",
      "refId": "A"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "red", "value": 80 },
          { "color": "dark-red", "value": 90 }
        ]
      }
    }
  }
}
```

### Stat Panel
```json
{
  "title": "Total Requests",
  "type": "stat",
  "targets": [
    {
      "expr": "sum(increase(http_requests_total[1h]))",
      "refId": "A"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "none",
      "color": {
        "mode": "thresholds"
      },
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "red", "value": 1000 }
        ]
      }
    }
  },
  "options": {
    "reduceOptions": {
      "values": false,
      "calcs": ["lastNotNull"],
      "fields": ""
    }
  }
}
```

### Heatmap Panel
```json
{
  "title": "Response Time Heatmap",
  "type": "heatmap",
  "targets": [
    {
      "expr": "rate(http_request_duration_seconds_bucket[5m])",
      "legendFormat": "{{le}}",
      "refId": "A"
    }
  ],
  "heatmap": {
    "buckets": 20,
    "hideZeroBuckets": true
  },
  "cards": {
    "cardPadding": null,
    "cardRound": null
  }
}
```

## Alerting

### Настройка алертов
```json
{
  "alert": {
    "name": "High CPU Usage",
    "message": "CPU usage is above 80%",
    "conditions": [
      {
        "evaluator": {
          "params": [80],
          "type": "gt"
        },
        "operator": {
          "type": "and"
        },
        "query": {
          "params": ["A", "5m", "now"]
        },
        "reducer": {
          "params": [],
          "type": "avg"
        },
        "type": "query"
      }
    ],
    "executionErrorState": "alerting",
    "frequency": "60s",
    "handler": 1,
    "noDataState": "no_value",
    "notifications": [
      {
        "id": 1
      }
    ]
  }
}
```

### Notification Channels

#### Email
```json
{
  "name": "email-alerts",
  "type": "email",
  "settings": {
    "addresses": "alerts@example.com,admin@example.com",
    "singleEmail": false
  }
}
```

#### Slack
```json
{
  "name": "slack-alerts",
  "type": "slack",
  "settings": {
    "url": "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX",
    "recipient": "#alerts",
    "username": "Grafana Alert",
    "icon_emoji": ":warning:"
  }
}
```

#### PagerDuty
```json
{
  "name": "pagerduty-alerts",
  "type": "pagerduty",
  "settings": {
    "integrationKey": "your-pagerduty-integration-key",
    "autoResolve": true,
    "severity": "critical"
  }
}
```

#### Webhook
```json
{
  "name": "webhook-alerts",
  "type": "webhook",
  "settings": {
    "url": "http://alertmanager:9093/api/v1/alerts",
    "httpMethod": "POST",
    "username": "",
    "password": ""
  }
}
```

## Плагины и расширения

### Установка плагинов
```bash
# Через grafana-cli
grafana-cli plugins install grafana-piechart-panel
grafana-cli plugins install grafana-worldmap-panel
grafana-cli plugins install grafana-clock-panel

# Обновление списка плагинов
grafana-cli plugins update-all

# Удаление плагина
grafana-cli plugins remove grafana-piechart-panel
```

### Популярные панели
```json
// Pie Chart Panel
{
  "title": "Request Methods Distribution",
  "type": "grafana-piechart-panel",
  "targets": [
    {
      "expr": "sum(http_requests_total) by (method)",
      "legendFormat": "{{method}}",
      "refId": "A"
    }
  ]
}

// Worldmap Panel
{
  "title": "Global User Distribution",
  "type": "grafana-worldmap-panel",
  "targets": [
    {
      "expr": "users_online",
      "legendFormat": "{{country}}",
      "refId": "A"
    }
  ],
  "worldmap": {
    "center": "Europe",
    "showLegend": true,
    "circleMaxSize": 30,
    "circleMinSize": 2
  }
}
```

### Кастомные плагины
```typescript
// plugin.json
{
  "type": "panel",
  "name": "Custom Metrics Panel",
  "id": "custom-metrics-panel",
  "info": {
    "description": "Custom panel for displaying metrics",
    "author": {
      "name": "Your Name"
    },
    "keywords": ["metrics", "custom"],
    "logos": {
      "small": "img/logo.svg",
      "large": "img/logo.svg"
    },
    "links": [
      {
        "name": "GitHub",
        "url": "https://github.com/your-org/custom-metrics-panel"
      }
    ],
    "screenshots": [
      {
        "name": "Main view",
        "path": "img/screenshot.png"
      }
    ],
    "version": "1.0.0",
    "updated": "2023-01-01"
  },
  "dependencies": {
    "grafanaDependency": ">=8.0.0",
    "plugins": []
  }
}
```

## Управление пользователями и доступом

### Организации и команды
```sql
-- Создание организации
INSERT INTO org (name, created, updated) VALUES ('Production', NOW(), NOW());

-- Создание пользователя
INSERT INTO user (login, email, name, password, created, updated, is_admin)
VALUES ('john.doe', 'john@example.com', 'John Doe', '$2b$10$...', NOW(), NOW(), false);

-- Добавление пользователя в организацию
INSERT INTO org_user (org_id, user_id, role) VALUES (1, 1, 'Editor');

-- Создание команды
INSERT INTO team (org_id, name, email, created, updated) VALUES (1, 'DevOps Team', 'devops@example.com', NOW(), NOW());

-- Добавление пользователя в команду
INSERT INTO team_member (org_id, team_id, user_id, created) VALUES (1, 1, 1, NOW());
```

### Роли и разрешения
```json
{
  "permissions": [
    {
      "action": "dashboards:read",
      "scope": "dashboards:*"
    },
    {
      "action": "dashboards:write",
      "scope": "folders:1"
    },
    {
      "action": "datasources:read",
      "scope": "datasources:*"
    }
  ],
  "roles": [
    {
      "name": "Viewer",
      "description": "Can view dashboards",
      "permissions": [
        {
          "action": "dashboards:read",
          "scope": "dashboards:*"
        }
      ]
    },
    {
      "name": "Editor",
      "description": "Can edit dashboards",
      "permissions": [
        {
          "action": "dashboards:read",
          "scope": "dashboards:*"
        },
        {
          "action": "dashboards:write",
          "scope": "dashboards:*"
        }
      ]
    }
  ]
}
```

## Provisioning

### Дашборды через файлы
```yaml
# /etc/grafana/provisioning/dashboards/dashboard.yml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: true
    options:
      path: /var/lib/grafana/dashboards

# dashboard.json
{
  "dashboard": {
    "title": "System Overview",
    "tags": ["system", "auto-generated"],
    "timezone": "browser",
    "refresh": "30s",
    "panels": [
      {
        "id": 1,
        "title": "CPU Usage",
        "type": "graph",
        "gridPos": { "h": 8, "w": 12, "x": 0, "y": 0 },
        "targets": [
          {
            "expr": "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)",
            "legendFormat": "{{instance}}"
          }
        ]
      }
    ]
  },
  "overwrite": true
}
```

### Data Sources через файлы
```yaml
# /etc/grafana/provisioning/datasources/datasource.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
  - name: Elasticsearch
    type: elasticsearch
    access: proxy
    url: http://elasticsearch:9200
    database: "[metrics-]YYYY.MM.DD"
    jsonData:
      esVersion: 70
      timeField: "@timestamp"
```

## API и автоматизация

### REST API
```bash
# Получение всех дашбордов
curl -H "Authorization: Bearer YOUR_API_TOKEN" \
  http://localhost:3000/api/search?query=*

# Создание дашборда
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_TOKEN" \
  -d @dashboard.json \
  http://localhost:3000/api/dashboards/db

# Обновление data source
curl -X PUT \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_TOKEN" \
  -d @datasource.json \
  http://localhost:3000/api/datasources/1

# Создание алерта
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_TOKEN" \
  -d @alert.json \
  http://localhost:3000/api/alerts
```

### Terraform провизининг
```hcl
resource "grafana_dashboard" "metrics" {
  config_json = file("${path.module}/dashboards/metrics.json")
  folder      = grafana_folder.monitoring.id
}

resource "grafana_data_source" "prometheus" {
  type       = "prometheus"
  name       = "prometheus"
  url        = "http://prometheus:9090"
  is_default = true
}

resource "grafana_alert_notification" "slack" {
  name = "slack-alerts"
  type = "slack"

  settings = {
    url   = var.slack_webhook_url
    recipient = "#alerts"
  }
}

resource "grafana_folder" "monitoring" {
  title = "Monitoring"
}

resource "grafana_team" "devops" {
  name  = "DevOps"
  email = "devops@example.com"
  members = [
    "user1@example.com",
    "user2@example.com"
  ]
}
```

## Лучшие практики

### Производительность
```ini
# Оптимизация производительности
[server]
router_logging = false

[database]
max_open_conn = 100
max_idle_conn = 100
conn_max_lifetime = 14400

[metrics]
enabled = true
interval_seconds = 10
disable_total_stats = true

[analytics]
reporting_enabled = false
check_for_updates = false
```

### Безопасность
```ini
# Безопасная конфигурация
[security]
admin_user = admin
admin_password = ${GF_SECURITY_ADMIN_PASSWORD}
secret_key = ${GF_SECURITY_SECRET_KEY}
disable_gravatar = true
cookie_secure = true
cookie_samesite = strict

[auth]
disable_login_form = false
oauth_auto_login = false
signout_redirect_url = https://login.example.com/signout

[auth.anonymous]
enabled = false

[auth.jwt]
enabled = true
header_name = X-JWT-Assertion
email_claim = email
username_claim = username
jwk_set_url = https://login.example.com/.well-known/jwks.json
cache_ttl = 60m
```

### Масштабирование
```yaml
# Kubernetes HPA для Grafana
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: grafana-hpa
  namespace: monitoring
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: grafana
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80

# External PostgreSQL для высокой доступности
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
spec:
  template:
    spec:
      containers:
      - name: grafana
        env:
        - name: GF_DATABASE_TYPE
          value: "postgres"
        - name: GF_DATABASE_HOST
          value: "grafana-postgres"
        - name: GF_DATABASE_SSL_MODE
          value: "require"
```

## Troubleshooting

### Распространенные проблемы
```bash
# Проверка логов
docker logs grafana

# Проверка конфигурации
grafana-cli config:check

# Проверка плагинов
grafana-cli plugins ls

# Очистка кэша
rm -rf /var/lib/grafana/cache/

# Перезапуск
docker restart grafana
```

### Диагностика проблем
```sql
-- Проверка подключения к БД
SELECT version();

-- Проверка пользователей
SELECT id, login, email FROM user;

-- Проверка дашбордов
SELECT id, title, slug FROM dashboard;

-- Проверка data sources
SELECT id, name, type, url FROM data_source;

-- Очистка сессий
DELETE FROM session WHERE created_at < NOW() - INTERVAL '30 days';
```

### Мониторинг Grafana
```promql
# Метрики Grafana (если включены)
grafana_stat_totals_dashboard
grafana_stat_total_active_users
grafana_api_response_status_total

# HTTP метрики
rate(http_requests_total{job="grafana"}[5m])
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket{job="grafana"}[5m]))

# Database метрики
rate(grafana_db_request_total[5m])
grafana_db_request_duration_seconds{quantile="0.95"}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Grafana](https://grafana.com/docs/)
- [Grafana GitHub](https://github.com/grafana/grafana)
- [Grafana Plugins](https://grafana.com/plugins/)
- [Grafana API](https://grafana.com/docs/grafana/latest/http_api/)
- [Prometheus Integration](https://grafana.com/docs/grafana/latest/datasources/prometheus/)

## См. также
- [Prometheus](monitoring/prometheus.md) - Сбор метрик
- [Distributed Tracing](monitoring/distributed-tracing.md) - Распределенное трассирование
- [Micrometer](java-micrometer.md) - Метрики для JVM
