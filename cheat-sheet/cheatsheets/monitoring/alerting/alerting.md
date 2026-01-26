# Системы алертинга для Java

Комплексное руководство по настройке и использованию систем алертинга в Java-приложениях: Alertmanager, PagerDuty, Slack, email notifications и best practices.

**Дата последнего обновления:** 2026-01-22

## Полезные ссылки

### Официальная документация
- [Prometheus Alertmanager](https://prometheus.io/docs/alerting/latest/alertmanager/) - Официальная документация
- [Alertmanager Configuration](https://prometheus.io/docs/alerting/latest/configuration/) - Конфигурация
- [Alerting Rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/) - Правила алертинга

### Java интеграции
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) - Health checks
- [Micrometer](https://micrometer.io/) - Metrics для алертинга
- [Alertmanager Java Client](https://github.com/prometheus/alertmanager) - Java клиент

### Статьи и туториалы
- [Alerting Best Practices](https://prometheus.io/docs/practices/alerting/)
- [Alertmanager Tutorial](https://prometheus.io/docs/alerting/latest/alertmanager/)
- [Avoiding Alert Fatigue](https://www.pagerduty.com/blog/avoid-alert-fatigue/)

### См. также
- `monitoring/prometheus.md` - Prometheus метрики
- `monitoring/grafana.md` - Grafana алерты
- `logging/centralized-logging.md` - Логи для алертов
- `monitoring/metrics.md` - Метрики для алертинга

## Содержание

- [Введение в алертинг](#введение-в-алертинг)
- [Alertmanager архитектура](#alertmanager-архитектура)
- [Настройка Alertmanager](#настройка-alertmanager)
- [Java интеграция](#java-интеграция)
- [Alert rules](#alert-rules)
- [Notification channels](#notification-channels)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в алертинг

**Алертинг** — это процесс автоматического обнаружения проблем в системе и уведомления ответственных лиц. В контексте мониторинга алертинг позволяет своевременно реагировать на инциденты, предотвращать downtime и обеспечивать SLA.

### Почему важен алертинг?

Алертинг решает критические задачи:

1. **Early Detection** — Быстрое обнаружение проблем
2. **Automated Response** — Автоматизированная реакция на инциденты
3. **Reduced MTTR** — Сокращение времени восстановления
4. **SLA Compliance** — Обеспечение service level agreements
5. **Business Continuity** — Поддержание непрерывности бизнеса
6. **Team Coordination** — Координация работы команды

### Типы алертов

#### По severity
- **Critical** — Системный сбой, полный downtime
- **Warning** — Потенциальная проблема, degradation
- **Info** — Информационные уведомления
- **Resolved** — Проблема решена

#### По источнику
- **Infrastructure** — Проблемы с серверами, сетью
- **Application** — Ошибки в приложении
- **Business** — Бизнес-метрики (конверсия, revenue)
- **Security** — Security инциденты

#### По времени
- **Real-time** — Немедленное уведомление
- **Scheduled** — Регулярные отчеты
- **Escalation** — Эскалация при отсутствии реакции

## Alertmanager архитектура

### Компоненты системы

```
┌─────────────────────────────────────────────────────────────────┐
│                    Alert Sources                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Prometheus │ Grafana │ Custom Applications              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Alertmanager                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Alert Aggregation │ Deduplication │ Silencing           │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Routing & Grouping                            │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Group by Labels │ Route by Matchers │ Inhibition        │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Notification Channels                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Email │ Slack │ PagerDuty │ Webhook │ SMS              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### Alert lifecycle

#### 1. Alert Generation
```yaml
# Prometheus rule
- alert: HighErrorRate
  expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
  for: 5m
  labels:
    severity: critical
    team: backend
  annotations:
    summary: "High error rate detected"
    description: "Error rate is {{ $value }} errors per second"
```

#### 2. Alert Routing
```yaml
# Alertmanager routing
route:
  group_by: ['alertname', 'cluster']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: 'backend-team'
  routes:
  - match:
      team: backend
      severity: critical
    receiver: 'backend-pager'
  - match:
      team: frontend
    receiver: 'frontend-slack'
```

#### 3. Alert Notification
```yaml
# Notification configuration
receivers:
- name: 'backend-pager'
  pagerduty_configs:
  - service_key: 'your-pagerduty-service-key'
    description: '{{ .GroupLabels.alertname }}: {{ .CommonAnnotations.summary }}'
```

#### 4. Alert Resolution
```yaml
# Alert resolution
- alert: ServiceRestored
  expr: up == 1
  for: 1m
  labels:
    severity: info
  annotations:
    summary: "Service {{ $labels.instance }} is back up"
```

## Настройка Alertmanager

### Установка Alertmanager

#### Docker
```bash
# Запуск Alertmanager
docker run -d \
  -p 9093:9093 \
  --name alertmanager \
  -v $(pwd)/alertmanager.yml:/etc/alertmanager/config.yml \
  prom/alertmanager:latest \
  --config.file=/etc/alertmanager/config.yml \
  --storage.path=/alertmanager
```

#### Linux
```bash
# Скачивание
wget https://github.com/prometheus/alertmanager/releases/download/v0.25.0/alertmanager-0.25.0.linux-amd64.tar.gz
tar xvfz alertmanager-0.25.0.linux-amd64.tar.gz
cd alertmanager-0.25.0.linux-amd64/

# Запуск
./alertmanager --config.file=alertmanager.yml
```

#### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: alertmanager
  namespace: monitoring
spec:
  replicas: 2
  selector:
    matchLabels:
      app: alertmanager
  template:
    metadata:
      labels:
        app: alertmanager
    spec:
      containers:
      - name: alertmanager
        image: prom/alertmanager:latest
        ports:
        - containerPort: 9093
          name: http
        volumeMounts:
        - name: config
          mountPath: /etc/alertmanager
        - name: storage
          mountPath: /alertmanager
      volumes:
      - name: config
        configMap:
          name: alertmanager-config
      - name: storage
        persistentVolumeClaim:
          claimName: alertmanager-pvc
```

### Конфигурация Alertmanager

#### Basic configuration
```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alerts@example.com'
  smtp_auth_username: 'alerts@example.com'
  smtp_auth_password: 'your-password'

templates:
  - '/etc/alertmanager/templates/*.tmpl'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'email-notifications'
  routes:
  - match:
      severity: critical
    receiver: 'critical-notifications'
  - match:
      team: backend
    receiver: 'backend-team'

receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'team@example.com'
    send_resolved: true

- name: 'critical-notifications'
  email_configs:
  - to: 'oncall@example.com'
    send_resolved: true
  slack_configs:
  - api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'
    channel: '#alerts-critical'
    send_resolved: true

- name: 'backend-team'
  slack_configs:
  - api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'
    channel: '#backend-alerts'
    send_resolved: true
```

#### Advanced configuration
```yaml
global:
  smtp_smarthost: 'smtp.example.com:587'
  smtp_from: 'alertmanager@example.com'
  smtp_auth_username: 'alertmanager@example.com'
  smtp_auth_password: 'password'
  slack_api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'

templates:
  - '/etc/alertmanager/templates/*.tmpl'

route:
  group_by: ['alertname', 'cluster', 'service']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: 'default-receiver'
  
  routes:
  # Critical alerts - immediate notification
  - match:
      severity: critical
    receiver: 'critical-pager'
    continue: true
    
  # Infrastructure alerts
  - match:
      team: infrastructure
    receiver: 'infra-slack'
    routes:
    - match:
        severity: critical
      receiver: 'infra-pager'
      
  # Application alerts by team
  - match:
      team: backend
    receiver: 'backend-slack'
  - match:
      team: frontend
    receiver: 'frontend-slack'
  - match:
      team: mobile
    receiver: 'mobile-slack'

inhibit_rules:
  # Suppress warnings if critical alert exists
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['alertname', 'cluster', 'service']

receivers:
- name: 'default-receiver'
  email_configs:
  - to: 'devops@example.com'

- name: 'critical-pager'
  pagerduty_configs:
  - service_key: '{{ .GroupLabels.service_key }}'
    description: '{{ .GroupLabels.alertname }}: {{ .CommonAnnotations.summary }}'
    details:
      firing: '{{ .Alerts.Firing | len }}'
      resolved: '{{ .Alerts.Resolved | len }}'

- name: 'infra-slack'
  slack_configs:
  - channel: '#infrastructure'
    title: '{{ .GroupLabels.alertname }}'
    text: '{{ .CommonAnnotations.description }}'
    send_resolved: true

- name: 'backend-slack'
  slack_configs:
  - channel: '#backend-alerts'
    title: '{{ .GroupLabels.alertname }}'
    text: '{{ .CommonAnnotations.description }}'
    send_resolved: true

- name: 'frontend-slack'
  slack_configs:
  - channel: '#frontend-alerts'
    title: '{{ .GroupLabels.alertname }}'
    text: '{{ .CommonAnnotations.description }}'
    send_resolved: true

- name: 'mobile-slack'
  slack_configs:
  - channel: '#mobile-alerts'
    title: '{{ .GroupLabels.alertname }}'
    text: '{{ .CommonAnnotations.description }}'
    send_resolved: true
```

## Java интеграция

### Spring Boot Health Checks

#### Custom health indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // Test database connection
            connection.createStatement().execute("SELECT 1");
            
            return Health.up()
                .withDetail("database", "available")
                .build();
                
        } catch (SQLException e) {
            return Health.down()
                .withDetail("database", "unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Health health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "http://external-service/health", String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                    .withDetail("external-service", "available")
                    .withDetail("response-time", "fast")
                    .build();
            } else {
                return Health.down()
                    .withDetail("external-service", "unavailable")
                    .withDetail("status-code", response.getStatusCode())
                    .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("external-service", "unreachable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

#### Micrometer metrics for alerting
```java
@Service
public class AlertMetricsService {

    private final Counter errorCounter;
    private final Counter successCounter;
    private final Gauge activeConnectionsGauge;
    private final Timer requestTimer;

    @Autowired
    public AlertMetricsService(MeterRegistry meterRegistry) {
        this.errorCounter = Counter.builder("application_errors_total")
            .description("Total number of application errors")
            .tags("application", "user-service")
            .register(meterRegistry);

        this.successCounter = Counter.builder("application_success_total")
            .description("Total number of successful operations")
            .tags("application", "user-service")
            .register(meterRegistry);

        this.activeConnectionsGauge = Gauge.builder("active_connections", this, 
                AlertMetricsService::getActiveConnections)
            .description("Number of active connections")
            .tags("application", "user-service")
            .register(meterRegistry);

        this.requestTimer = Timer.builder("http_request_duration_seconds")
            .description("HTTP request duration")
            .tags("application", "user-service")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
    }

    public void recordError(String errorType) {
        errorCounter.increment();
        // Additional error-specific metrics
    }

    public void recordSuccess() {
        successCounter.increment();
    }

    public long getActiveConnections() {
        // Return actual active connections count
        return connectionPool.getActiveConnections();
    }

    public void recordRequest(long durationMs, boolean success) {
        Timer.Sample sample = Timer.start(meterRegistry);
        // Simulate request processing
        sample.stop(Timer.builder("request_duration")
            .tag("success", String.valueOf(success))
            .register(meterRegistry));
    }
}
```

### Custom alert generation

#### Alert service
```java
@Service
public class AlertService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String ALERTMANAGER_URL = "http://alertmanager:9093/api/v2/alerts";

    public void sendAlert(String alertName, String description, AlertSeverity severity) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("labels", Map.of(
            "alertname", alertName,
            "severity", severity.toString().toLowerCase(),
            "service", "user-service",
            "instance", getInstanceId()
        ));
        
        alert.put("annotations", Map.of(
            "summary", alertName,
            "description", description,
            "runbook_url", "https://runbook.example.com/" + alertName.toLowerCase()
        ));
        
        alert.put("startsAt", OffsetDateTime.now().toString());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<Map<String, Object>>> request = 
                new HttpEntity<>(List.of(alert), headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                ALERTMANAGER_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Alert sent successfully", kv("alertName", alertName));
            } else {
                logger.error("Failed to send alert", 
                           kv("alertName", alertName),
                           kv("statusCode", response.getStatusCode()));
            }
            
        } catch (Exception e) {
            logger.error("Error sending alert to Alertmanager", 
                        kv("alertName", alertName), e);
        }
    }

    public void resolveAlert(String alertName) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("labels", Map.of(
            "alertname", alertName,
            "service", "user-service"
        ));
        
        alert.put("endsAt", OffsetDateTime.now().toString());
        
        // Send resolution alert
        sendResolutionAlert(alert);
    }

    private String getInstanceId() {
        return System.getenv().getOrDefault("HOSTNAME", "unknown");
    }
}

enum AlertSeverity {
    INFO, WARNING, ERROR, CRITICAL
}
```

#### Health check endpoints
```java
@RestController
@RequestMapping("/actuator/health")
public class CustomHealthController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private HealthIndicator databaseHealth;

    @Autowired
    private HealthIndicator externalServiceHealth;

    @GetMapping("/detailed")
    public Map<String, Object> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        Health dbHealth = databaseHealth.health();
        Health extHealth = externalServiceHealth.health();
        
        health.put("database", dbHealth.getStatus().toString());
        health.put("externalService", extHealth.getStatus().toString());
        health.put("overall", calculateOverallHealth(dbHealth, extHealth));
        
        // Send alert if overall health is down
        if ("DOWN".equals(health.get("overall"))) {
            alertService.sendAlert(
                "ServiceHealthDegraded",
                "One or more health checks are failing",
                AlertSeverity.WARNING
            );
        }
        
        return health;
    }

    @GetMapping("/metrics")
    public Map<String, Object> healthMetrics() {
        // Return health metrics for monitoring
        return Map.of(
            "uptime", getUptime(),
            "memoryUsage", getMemoryUsage(),
            "cpuUsage", getCpuUsage(),
            "activeThreads", getActiveThreads()
        );
    }

    private String calculateOverallHealth(Health... healths) {
        for (Health health : healths) {
            if (health.getStatus() != Status.UP) {
                return "DOWN";
            }
        }
        return "UP";
    }

    private long getUptime() {
        return System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    private double getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        return (double) heapUsage.getUsed() / heapUsage.getMax();
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getProcessCpuLoad();
        }
        return 0.0;
    }

    private int getActiveThreads() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.getThreadCount();
    }
}
```

## Alert rules

### Prometheus alert rules

#### Infrastructure alerts
```yaml
groups:
- name: infrastructure
  rules:
  
  # Host down
  - alert: HostDown
    expr: up == 0
    for: 5m
    labels:
      severity: critical
      team: infrastructure
    annotations:
      summary: "Host {{ $labels.instance }} is down"
      description: "Host {{ $labels.instance }} has been down for more than 5 minutes"
      runbook_url: "https://runbook.example.com/host-down"
  
  # High CPU usage
  - alert: HighCpuUsage
    expr: cpu_usage_percent > 90
    for: 5m
    labels:
      severity: warning
      team: infrastructure
    annotations:
      summary: "High CPU usage on {{ $labels.instance }}"
      description: "CPU usage is {{ $value }}%"
      runbook_url: "https://runbook.example.com/high-cpu"
  
  # Low disk space
  - alert: LowDiskSpace
    expr: (disk_free_bytes / disk_total_bytes) * 100 < 10
    for: 10m
    labels:
      severity: critical
      team: infrastructure
    annotations:
      summary: "Low disk space on {{ $labels.instance }}"
      description: "Only {{ $value }}% disk space remaining"
      runbook_url: "https://runbook.example.com/low-disk-space"
  
  # High memory usage
  - alert: HighMemoryUsage
    expr: (memory_used_bytes / memory_total_bytes) * 100 > 90
    for: 5m
    labels:
      severity: warning
      team: infrastructure
    annotations:
      summary: "High memory usage on {{ $labels.instance }}"
      description: "Memory usage is {{ $value }}%"
      runbook_url: "https://runbook.example.com/high-memory"
```

#### Application alerts
```yaml
groups:
- name: application
  rules:
  
  # High error rate
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) > 0.05
    for: 5m
    labels:
      severity: critical
      team: backend
    annotations:
      summary: "High error rate on {{ $labels.service }}"
      description: "{{ $value | printf \"%.2f\" }}% of requests are failing"
      runbook_url: "https://runbook.example.com/high-error-rate"
  
  # Slow response time
  - alert: SlowResponseTime
    expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2.0
    for: 5m
    labels:
      severity: warning
      team: backend
    annotations:
      summary: "Slow response time on {{ $labels.service }}"
      description: "95th percentile response time is {{ $value }}s"
      runbook_url: "https://runbook.example.com/slow-response"
  
  # Database connection pool exhausted
  - alert: DbConnectionPoolExhausted
    expr: db_connection_pool_active_connections / db_connection_pool_max_size > 0.9
    for: 3m
    labels:
      severity: critical
      team: backend
    annotations:
      summary: "Database connection pool exhausted"
      description: "{{ $value | printf \"%.2f\" }}% of connections are in use"
      runbook_url: "https://runbook.example.com/db-connection-pool"
  
  # Application not responding
  - alert: ApplicationNotResponding
    expr: http_requests_total offset 5m unless http_requests_total
    for: 1m
    labels:
      severity: critical
      team: backend
    annotations:
      summary: "Application {{ $labels.service }} is not responding"
      description: "No requests received in the last 5 minutes"
      runbook_url: "https://runbook.example.com/application-not-responding"
```

#### Business alerts
```yaml
groups:
- name: business
  rules:
  
  # Low conversion rate
  - alert: LowConversionRate
    expr: conversion_rate_percent < 2.0
    for: 15m
    labels:
      severity: warning
      team: business
    annotations:
      summary: "Low conversion rate detected"
      description: "Conversion rate dropped to {{ $value }}%"
      runbook_url: "https://runbook.example.com/low-conversion"
  
  # High cart abandonment
  - alert: HighCartAbandonment
    expr: cart_abandonment_rate_percent > 75
    for: 10m
    labels:
      severity: warning
      team: business
    annotations:
      summary: "High cart abandonment rate"
      description: "Cart abandonment rate is {{ $value }}%"
      runbook_url: "https://runbook.example.com/cart-abandonment"
  
  # Revenue drop
  - alert: RevenueDrop
    expr: (revenue_current_hour / revenue_previous_hour) < 0.7
    for: 1h
    labels:
      severity: critical
      team: business
    annotations:
      summary: "Significant revenue drop detected"
      description: "Revenue dropped by {{ $value | printf \"%.1f\" }}% compared to previous hour"
      runbook_url: "https://runbook.example.com/revenue-drop"
```

## Notification channels

### Email notifications

#### SMTP configuration
```yaml
receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'alerts@example.com'
    from: 'alertmanager@example.com'
    smarthost: 'smtp.gmail.com:587'
    auth_username: 'alertmanager@example.com'
    auth_password: 'your-app-password'
    send_resolved: true
    html: '{{ template "email.html" . }}'
    headers:
      Subject: '[{{ .Status | toUpper }}{{ if eq .Status "firing" }}:{{ .Alerts.Firing | len }}{{ end }}] {{ .GroupLabels.alertname }}'
```

#### Email templates
```html
<!-- /etc/alertmanager/templates/email.html -->
<!DOCTYPE html>
<html>
<head>
    <title>{{ .GroupLabels.alertname }}</title>
</head>
<body>
    <h1>{{ .GroupLabels.alertname }}</h1>
    
    <p><strong>Status:</strong> {{ .Status }}</p>
    <p><strong>Started:</strong> {{ .GroupLabels.startsAt }}</p>
    
    <h2>Alerts</h2>
    {{ range .Alerts }}
    <div style="border: 1px solid #ccc; margin: 10px; padding: 10px;">
        <h3>{{ .Annotations.summary }}</h3>
        <p>{{ .Annotations.description }}</p>
        <p><strong>Labels:</strong> {{ .Labels }}</p>
        <p><strong>Started:</strong> {{ .StartsAt }}</p>
        {{ if .EndsAt }}
        <p><strong>Ended:</strong> {{ .EndsAt }}</p>
        {{ end }}
    </div>
    {{ end }}
    
    <h2>Runbook</h2>
    <p><a href="{{ .CommonAnnotations.runbook_url }}">View Runbook</a></p>
</body>
</html>
```

### Slack notifications

#### Slack webhook
```yaml
receivers:
- name: 'slack-notifications'
  slack_configs:
  - api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'
    channel: '#alerts'
    username: 'Alertmanager'
    icon_emoji: ':warning:'
    send_resolved: true
    title: '{{ .GroupLabels.alertname }}'
    text: '{{ .CommonAnnotations.description }}'
    color: '{{ if eq .Status "firing" }}danger{{ else }}good{{ end }}'
    fields:
    - title: 'Severity'
      value: '{{ .GroupLabels.severity }}'
    - title: 'Service'
      value: '{{ .GroupLabels.service }}'
    - title: 'Instance'
      value: '{{ .GroupLabels.instance }}'
    actions:
    - type: button
      text: 'View Dashboard'
      url: 'https://grafana.example.com/d/{{ .GroupLabels.dashboard_uid }}'
    - type: button
      text: 'View Runbook'
      url: '{{ .CommonAnnotations.runbook_url }}'
```

#### Slack app integration
```java
@Service
public class SlackAlertService {

    private static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/YOUR/WEBHOOK";

    @Autowired
    private RestTemplate restTemplate;

    public void sendSlackAlert(Alert alert) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("channel", "#alerts");
        payload.put("username", "Alert Bot");
        payload.put("icon_emoji", getIconForSeverity(alert.getSeverity()));
        
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("fallback", alert.getSummary());
        attachment.put("color", getColorForSeverity(alert.getSeverity()));
        attachment.put("title", alert.getSummary());
        attachment.put("text", alert.getDescription());
        
        Map<String, String> field1 = new HashMap<>();
        field1.put("title", "Severity");
        field1.put("value", alert.getSeverity().toString());
        field1.put("short", true);
        
        Map<String, String> field2 = new HashMap<>();
        field2.put("title", "Service");
        field2.put("value", alert.getService());
        field2.put("short", true);
        
        attachment.put("fields", Arrays.asList(field1, field2));
        
        if (alert.getRunbookUrl() != null) {
            Map<String, String> action = new HashMap<>();
            action.put("type", "button");
            action.put("text", "View Runbook");
            action.put("url", alert.getRunbookUrl());
            attachment.put("actions", Arrays.asList(action));
        }
        
        payload.put("attachments", Arrays.asList(attachment));
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                SLACK_WEBHOOK_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Slack alert sent successfully", kv("alert", alert.getSummary()));
            } else {
                logger.error("Failed to send Slack alert", 
                           kv("alert", alert.getSummary()),
                           kv("statusCode", response.getStatusCode()));
            }
            
        } catch (Exception e) {
            logger.error("Error sending Slack alert", kv("alert", alert.getSummary()), e);
        }
    }

    private String getIconForSeverity(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> ":fire:";
            case WARNING -> ":warning:";
            case INFO -> ":information_source:";
        };
    }

    private String getColorForSeverity(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "danger";
            case WARNING -> "warning";
            case INFO -> "good";
        };
    }
}
```

### PagerDuty integration

#### PagerDuty configuration
```yaml
receivers:
- name: 'pagerduty-critical'
  pagerduty_configs:
  - service_key: 'your-pagerduty-service-key'
    description: '{{ .GroupLabels.alertname }}: {{ .CommonAnnotations.summary }}'
    client: 'Alertmanager'
    client_url: 'https://alertmanager.example.com'
    details:
      firing: '{{ .Alerts.Firing | len }}'
      resolved: '{{ .Alerts.Resolved | len }}'
      severity: '{{ .GroupLabels.severity }}'
      service: '{{ .GroupLabels.service }}'
      instance: '{{ .GroupLabels.instance }}'
      description: '{{ .CommonAnnotations.description }}'
      runbook_url: '{{ .CommonAnnotations.runbook_url }}'
    send_resolved: true
```

#### PagerDuty Java client
```java
@Service
public class PagerDutyAlertService {

    private static final String PAGERDUTY_API_URL = "https://events.pagerduty.com/v2/enqueue";
    private static final String ROUTING_KEY = "your-pagerduty-routing-key";

    @Autowired
    private RestTemplate restTemplate;

    public void sendPagerDutyAlert(Alert alert) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("routing_key", ROUTING_KEY);
        payload.put("event_action", "trigger");
        
        Map<String, Object> dedupKey = new HashMap<>();
        dedupKey.put("alertname", alert.getName());
        dedupKey.put("service", alert.getService());
        payload.put("dedup_key", dedupKey.toString());
        
        Map<String, Object> eventPayload = new HashMap<>();
        eventPayload.put("summary", alert.getSummary());
        eventPayload.put("severity", mapSeverity(alert.getSeverity()));
        eventPayload.put("source", alert.getService());
        eventPayload.put("component", alert.getInstance());
        eventPayload.put("group", alert.getTeam());
        eventPayload.put("class", alert.getName());
        eventPayload.put("custom_details", Map.of(
            "description", alert.getDescription(),
            "runbook_url", alert.getRunbookUrl(),
            "labels", alert.getLabels()
        ));
        
        payload.put("payload", eventPayload);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                PAGERDUTY_API_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("PagerDuty alert sent successfully", kv("alert", alert.getName()));
            } else {
                logger.error("Failed to send PagerDuty alert", 
                           kv("alert", alert.getName()),
                           kv("statusCode", response.getStatusCode()));
            }
            
        } catch (Exception e) {
            logger.error("Error sending PagerDuty alert", kv("alert", alert.getName()), e);
        }
    }

    public void resolvePagerDutyAlert(String alertName, String service) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("routing_key", ROUTING_KEY);
        payload.put("event_action", "resolve");
        payload.put("dedup_key", Map.of("alertname", alertName, "service", service).toString());
        
        // Send resolution event
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            restTemplate.postForEntity(PAGERDUTY_API_URL, request, String.class);
            
        } catch (Exception e) {
            logger.error("Error resolving PagerDuty alert", 
                        kv("alert", alertName), kv("service", service), e);
        }
    }

    private String mapSeverity(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "critical";
            case WARNING -> "warning";
            case ERROR -> "error";
            case INFO -> "info";
        };
    }
}
```

## Best practices

### 1. Alert design

#### Alert naming conventions
```yaml
# Good alert names
- alert: HttpRequestRateHigh
- alert: DatabaseConnectionPoolExhausted
- alert: MemoryUsageCritical
- alert: ServiceResponseTimeSlow

# Bad alert names
- alert: Alert1
- alert: Problem
- alert: HighValue
```

#### Alert descriptions
```yaml
# Good descriptions
- alert: HttpRequestRateHigh
  annotations:
    summary: "HTTP request rate is too high"
    description: "HTTP request rate for {{ $labels.service }} is {{ $value | printf \"%.2f\" }} req/s, which is above the threshold of 100 req/s"
    runbook_url: "https://runbook.example.com/high-request-rate"

# Bad descriptions
- alert: HighErrorRate
  annotations:
    summary: "Error"
    description: "High"
```

#### Alert thresholds
```yaml
# Use reasonable thresholds
- alert: HighCpuUsage
  expr: cpu_usage_percent > 90  # Reasonable threshold
  for: 5m                      # Allow some time for spikes

# Avoid flapping
- alert: ServiceDown
  expr: up == 0
  for: 5m                      # Don't alert immediately

# Use percentages when appropriate
- alert: LowDiskSpace
  expr: (disk_free_bytes / disk_total_bytes) * 100 < 10
```

### 2. Alert routing

#### Team-based routing
```yaml
route:
  receiver: 'default'
  routes:
  # Infrastructure team
  - match:
      team: infrastructure
    receiver: 'infra-pager'
    routes:
    - match:
        severity: critical
      receiver: 'infra-immediate'
      
  # Backend team
  - match:
      team: backend
    receiver: 'backend-slack'
    routes:
    - match:
        severity: critical
      receiver: 'backend-pager'
      
  # Frontend team
  - match:
      team: frontend
    receiver: 'frontend-slack'
```

#### Time-based routing
```yaml
# Route to different receivers based on time
routes:
- match:
    severity: critical
  receiver: 'oncall-pager'
  routes:
  # Business hours - route to primary oncall
  - match:
      business_hours: 'true'
    receiver: 'primary-oncall'
  # After hours - route to secondary oncall
  - match:
      business_hours: 'false'
    receiver: 'secondary-oncall'
```

### 3. Alert fatigue prevention

#### Alert grouping
```yaml
route:
  group_by: ['alertname', 'cluster', 'service']
  group_wait: 30s      # Wait before sending first notification
  group_interval: 5m   # Wait between notifications
  repeat_interval: 4h  # Repeat notifications every 4 hours
```

#### Alert inhibition
```yaml
inhibit_rules:
# Don't send warnings if critical alert exists
- source_match:
    severity: 'critical'
  target_match:
    severity: 'warning'
  equal: ['alertname', 'cluster', 'service']

# Don't alert on slow response if service is down
- source_match:
    alertname: 'ServiceDown'
  target_match:
    alertname: 'SlowResponseTime'
  equal: ['service']
```

#### Alert maintenance
```yaml
# Regularly review and update alerts
- alert: AlertRulesStale
  expr: time() - alert_rules_last_update > 86400 * 30  # 30 days
  labels:
    severity: info
  annotations:
    summary: "Alert rules haven't been updated in 30 days"
    description: "Consider reviewing alert thresholds and rules"

# Monitor alert effectiveness
- alert: TooManyAlerts
  expr: rate(alerts_total[1h]) > 10
  labels:
    severity: warning
  annotations:
    summary: "Too many alerts firing"
    description: "Alert rate is {{ $value }} alerts per hour, consider reviewing alert rules"
```

### 4. Alert response

#### Runbooks
```yaml
# Every alert should have a runbook
- alert: DatabaseConnectionPoolExhausted
  annotations:
    summary: "Database connection pool exhausted"
    description: "{{ $value }}% of connections are in use"
    runbook_url: "https://runbook.example.com/database-connection-pool"

# Runbook content structure:
# 1. Overview
# 2. Symptoms
# 3. Diagnosis steps
# 4. Immediate actions
# 5. Root cause analysis
# 6. Prevention
# 7. Escalation contacts
```

#### Alert acknowledgment
```yaml
# Automatic acknowledgment for resolved alerts
receivers:
- name: 'auto-ack'
  webhook_configs:
  - url: 'http://alert-ack-service/api/ack'
    send_resolved: true

# Manual acknowledgment workflow
- alert: ManualAckRequired
  annotations:
    summary: "Manual acknowledgment required"
    description: "This alert requires manual acknowledgment before auto-resolution"
    ack_url: "https://alertmanager.example.com/#/alerts"
```

### 5. Alert monitoring

#### Alert metrics
```yaml
# Monitor alert system itself
- alert: AlertmanagerDown
  expr: up{job="alertmanager"} == 0
  labels:
    severity: critical
  annotations:
    summary: "Alertmanager is down"
    description: "Alertmanager has been down for 5 minutes"

# Alert effectiveness metrics
- alert: AlertsNotAcknowledged
  expr: alertmanager_alerts{state="firing"} and on(alertname) alertmanager_alerts{state="acknowledged"} == 0
  for: 1h
  labels:
    severity: info
  annotations:
    summary: "Alerts not acknowledged for 1 hour"
    description: "Consider reviewing alert response procedures"
```

#### Alert quality metrics
```java
@Service
public class AlertQualityMetrics {

    @Autowired
    private MeterRegistry meterRegistry;

    private final Counter falsePositiveCounter;
    private final Counter truePositiveCounter;
    private final Counter alertResolutionTime;

    public AlertQualityMetrics(MeterRegistry meterRegistry) {
        this.falsePositiveCounter = Counter.builder("alerts_false_positive_total")
            .description("Number of false positive alerts")
            .register(meterRegistry);

        this.truePositiveCounter = Counter.builder("alerts_true_positive_total")
            .description("Number of true positive alerts")
            .register(meterRegistry);

        this.alertResolutionTime = Counter.builder("alert_resolution_time_seconds")
            .description("Time taken to resolve alerts")
            .register(meterRegistry);
    }

    public void recordFalsePositive(String alertName) {
        falsePositiveCounter.increment();
        logger.info("False positive alert recorded", kv("alert", alertName));
    }

    public void recordTruePositive(String alertName) {
        truePositiveCounter.increment();
        logger.info("True positive alert recorded", kv("alert", alertName));
    }

    public void recordResolutionTime(String alertName, long resolutionTimeSeconds) {
        alertResolutionTime.increment(resolutionTimeSeconds);
        logger.info("Alert resolution time recorded", 
                   kv("alert", alertName),
                   kv("resolutionTime", resolutionTimeSeconds));
    }

    public Map<String, Object> getAlertQualityMetrics() {
        return Map.of(
            "falsePositiveRate", calculateFalsePositiveRate(),
            "averageResolutionTime", calculateAverageResolutionTime(),
            "alertEffectiveness", calculateAlertEffectiveness()
        );
    }

    private double calculateFalsePositiveRate() {
        double totalAlerts = falsePositiveCounter.count() + truePositiveCounter.count();
        return totalAlerts > 0 ? falsePositiveCounter.count() / totalAlerts : 0.0;
    }

    private double calculateAverageResolutionTime() {
        // Implementation to calculate average resolution time
        return 0.0; // Placeholder
    }

    private double calculateAlertEffectiveness() {
        // True positives / total alerts
        double totalAlerts = falsePositiveCounter.count() + truePositiveCounter.count();
        return totalAlerts > 0 ? truePositiveCounter.count() / totalAlerts : 0.0;
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Alerts not firing
```bash
# Check Prometheus alert rules
curl http://prometheus:9090/api/v1/rules

# Check alert status
curl http://prometheus:9090/api/v1/alerts

# Verify Alertmanager connectivity
curl http://alertmanager:9093/api/v2/status

# Check alertmanager logs
docker logs alertmanager
```

#### Duplicate alerts
```yaml
# Problem: Same alert firing multiple times
# Solution: Improve grouping

route:
  group_by: ['alertname', 'cluster', 'service', 'instance']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 1h
```

#### Alertmanager not receiving alerts
```yaml
# Check Prometheus configuration
alerting:
  alertmanagers:
  - static_configs:
    - targets:
      - alertmanager:9093

# Verify alertmanager endpoint
curl http://alertmanager:9093/api/v2/alerts
```

#### High alert volume
```yaml
# Implement alert aggregation
route:
  group_by: ['alertname', 'severity']
  group_wait: 1m
  group_interval: 10m

# Use inhibition rules
inhibit_rules:
- source_match:
    severity: 'critical'
  target_match:
    severity: 'warning'
  equal: ['service']
```

### Debug techniques

#### Alert simulation
```bash
# Manually trigger alert (for testing)
curl -X POST http://prometheus:9090/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[
    {
      "labels": {
        "alertname": "TestAlert",
        "severity": "warning",
        "service": "test-service"
      },
      "annotations": {
        "summary": "Test alert",
        "description": "This is a test alert"
      }
    }
  ]'
```

#### Alert validation
```java
public class AlertValidator {

    public void validateAlert(Alert alert) {
        List<String> errors = new ArrayList<>();

        // Check required fields
        if (alert.getName() == null || alert.getName().isEmpty()) {
            errors.add("Alert name is required");
        }

        if (alert.getSummary() == null || alert.getSummary().isEmpty()) {
            errors.add("Alert summary is required");
        }

        // Check severity
        if (alert.getSeverity() == null) {
            errors.add("Alert severity is required");
        }

        // Check runbook URL
        if (alert.getRunbookUrl() == null || alert.getRunbookUrl().isEmpty()) {
            errors.add("Runbook URL is required");
        }

        // Check labels
        if (alert.getLabels() == null || alert.getLabels().isEmpty()) {
            errors.add("Alert labels are required");
        }

        if (!errors.isEmpty()) {
            throw new AlertValidationException("Alert validation failed: " + String.join(", ", errors));
        }
    }

    public void validateAlertRule(AlertRule rule) {
        // Validate PromQL expression
        if (!isValidPromQL(rule.getExpression())) {
            throw new AlertValidationException("Invalid PromQL expression: " + rule.getExpression());
        }

        // Validate thresholds
        if (rule.getThreshold() <= 0) {
            throw new AlertValidationException("Threshold must be positive: " + rule.getThreshold());
        }

        // Validate duration
        if (rule.getForDuration() < 0) {
            throw new AlertValidationException("Duration cannot be negative: " + rule.getForDuration());
        }
    }

    private boolean isValidPromQL(String expression) {
        // Basic validation - in practice you'd use Prometheus API
        return expression != null && !expression.trim().isEmpty();
    }
}
```

#### Alert testing framework
```java
@SpringBootTest
public class AlertSystemTest {

    @Autowired
    private AlertService alertService;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    public void testAlertLifecycle() {
        // 1. Trigger alert condition
        Counter errorCounter = meterRegistry.counter("test_errors_total");
        errorCounter.increment(100);

        // 2. Wait for alert to fire (in real test, use Awaitility)
        await().atMost(30, SECONDS).until(() -> {
            // Check if alert was sent
            return alertWasSent("HighErrorRate");
        });

        // 3. Verify alert content
        Alert sentAlert = getLastSentAlert();
        assertThat(sentAlert.getName()).isEqualTo("HighErrorRate");
        assertThat(sentAlert.getSeverity()).isEqualTo(AlertSeverity.CRITICAL);

        // 4. Resolve alert condition
        errorCounter.increment(-100);

        // 5. Wait for alert resolution
        await().atMost(30, SECONDS).until(() -> {
            return alertWasResolved("HighErrorRate");
        });
    }

    @Test
    public void testAlertRouting() {
        // Test that alerts are routed to correct receivers
        alertService.sendAlert("TestAlert", "Test description", AlertSeverity.CRITICAL);

        await().atMost(10, SECONDS).until(() -> {
            return alertReceivedByPagerDuty("TestAlert");
        });

        assertThat(slackNotificationNotSent("TestAlert")).isTrue();
    }

    @Test
    public void testAlertDeduplication() {
        // Send multiple similar alerts
        for (int i = 0; i < 5; i++) {
            alertService.sendAlert("DuplicateAlert", "Test " + i, AlertSeverity.WARNING);
        }

        // Verify only one notification was sent
        await().atMost(10, SECONDS).until(() -> {
            return getNotificationCount("DuplicateAlert") == 1;
        });
    }
}
```

## Заключение

**Алертинг** — это критически важная часть системы мониторинга, которая обеспечивает своевременную реакцию на проблемы и поддержание SLA. Alertmanager от Prometheus предоставляет мощные возможности для маршрутизации, группировки и доставки алертов.

### Ключевые возможности:

1. **Alert Generation** — Правила алертинга в Prometheus
2. **Alert Routing** — Гибкая маршрутизация по лейблам
3. **Alert Grouping** — Группировка для предотвращения шума
4. **Inhibition** — Подавление зависимых алертов
5. **Notification Channels** — Email, Slack, PagerDuty, SMS
6. **Alert Lifecycle** — Создание, эскалация, разрешение

### Когда использовать алертинг:

✅ **Production системы** — Мониторинг критических метрик
✅ **SLA compliance** — Обеспечение service level agreements
✅ **Incident response** — Быстрая реакция на проблемы
✅ **Business monitoring** — Мониторинг бизнес-метрик
✅ **On-call rotations** — Управление дежурствами
✅ **Multi-team coordination** — Координация между командами

### Когда НЕ использовать:

❌ **Development** — Избыточный для разработки
❌ **Non-critical systems** — Для неважных систем
❌ **Manual monitoring** — Когда хватает ручного мониторинга
❌ **High-frequency alerts** — Для систем с частыми изменениями
❌ **Test environments** — Для тестовых сред

### Best practices summary:

1. **Alert Design** — Четкие имена, описания, пороги
2. **Alert Routing** — Маршрутизация по командам и severity
3. **Alert Fatigue** — Группировка, inhibition, maintenance
4. **Alert Response** — Runbooks, acknowledgment, escalation
5. **Alert Monitoring** — Метрики качества алертинга
6. **Alert Testing** — Тестирование алертов и маршрутизации
7. **Alert Quality** — Минимизация false positives

### Архитектурные решения:

- **Prometheus + Alertmanager** — Классический стек
- **Grafana Alerting** — Встроенные алерты в Grafana
- **PagerDuty** — Incident management платформа
- **Opsgenie** — Альтернатива PagerDuty
- **Custom integrations** — Webhooks, custom receivers

Alertmanager в комбинации с Prometheus образует надежную систему алертинга, обеспечивающую timely и effective incident response. 🚀
