# Grafana для Java

Комплексное руководство по использованию Grafana для визуализации метрик Java-приложений: создание dashboards, настройка алертинга, интеграция с Prometheus и продвинутые техники.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Grafana Documentation](https://grafana.com/docs/) - Основная документация
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/) - Создание dashboards
- [Grafana Panels](https://grafana.com/docs/grafana/latest/panels/) - Панели и визуализации

### Java интеграции
- [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) - UI для Spring Boot приложений
- [Micrometer + Grafana](https://micrometer.io/docs/registry/grafana) - Метрики для Grafana
- [Grafana Java Client](https://github.com/grafana/grafana-api-golang) - API клиент

### Dashboard примеры
- [Grafana Dashboards Gallery](https://grafana.com/grafana/dashboards/) - Готовые dashboards
- [JVM Dashboard](https://grafana.com/grafana/dashboards/4701) - Dashboard для JVM метрик
- [Spring Boot Dashboard](https://grafana.com/grafana/dashboards/11378) - Dashboard для Spring Boot

### См. также
- `monitoring/prometheus.md` - Сбор метрик с Prometheus
- `monitoring/alerting.md` - Система алертинга
- `monitoring/metrics.md` - Метрики в приложениях

## Содержание

- [Введение в Grafana](#введение-в-grafana)
- [Установка и настройка](#установка-и-настройка)
- [Источники данных](#источники-данных)
- [Создание dashboards](#создание-dashboards)
- [Панели визуализации](#панели-визуализации)
- [Spring Boot интеграция](#spring-boot-интеграция)
- [JVM monitoring dashboard](#jvm-monitoring-dashboard)
- [Application metrics dashboard](#application-metrics-dashboard)
- [Business metrics dashboard](#business-metrics-dashboard)
- [Алертинг в Grafana](#алертинг-в-grafana)
- [Templates и variables](#templates-и-variables)
- [Permissions и security](#permissions-и-security)
- [Performance optimization](#performance-optimization)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Grafana

**Grafana** — это платформа для мониторинга и observability с открытым исходным кодом, которая позволяет создавать красивые и информативные dashboards для визуализации метрик из различных источников данных.

### Почему Grafana?

Grafana предоставляет мощные возможности визуализации:

1. **Универсальная платформа** — поддержка 50+ источников данных
2. **Интерактивные dashboards** — drill-down и exploration
3. **Rich visualizations** — графики, таблицы, карты, тепловые карты
4. **Алертинг** — встроенная система оповещений
5. **Templates и variables** — динамические dashboards
6. **Плагины** — расширяемая экосистема
7. **Multi-tenancy** — поддержка нескольких организаций
8. **Real-time updates** — живые dashboards

### Основные компоненты

#### Data Sources
- **Prometheus** — основной источник для метрик
- **InfluxDB** — time-series database
- **Elasticsearch** — логи и анализ
- **CloudWatch** — AWS метрики
- **MySQL/PostgreSQL** — реляционные базы данных
- **JSON API** — REST API endpoints

#### Dashboards
- **Panels** — индивидуальные визуализации
- **Rows** — группировка панелей
- **Variables** — динамические параметры
- **Annotations** — метки событий
- **Links** — навигация между dashboards

#### Users и Organizations
- **Users** — индивидуальные пользователи
- **Teams** — группы пользователей
- **Organizations** — изоляция данных
- **Permissions** — granular доступ

## Установка и настройка

### Установка Grafana

**Grafana** может быть установлена различными способами: Docker, Kubernetes, бинарные файлы или пакеты. Выбор метода зависит от среды развертывания и требований к масштабируемости.

#### Docker установка

**Docker** обеспечивает самый простой способ запуска Grafana для разработки и тестирования. Контейнеры Grafana полностью изолированы и легко конфигурируемы.

```bash
# Базовый запуск Grafana с persistent storage
docker run -d \
  --name grafana \
  # Открываем порт 3000 для веб-интерфейса
  -p 3000:3000 \
  # Монтируем volume для хранения данных (dashboards, users, configs)
  -v grafana-storage:/var/lib/grafana \
  # Устанавливаем пароль администратора
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  # Используем последнюю стабильную версию
  grafana/grafana:latest

# Проверка запуска
docker ps | grep grafana
docker logs grafana
```

**Запуск с внешней базой данных:**
```bash
# Grafana с PostgreSQL для production
docker run -d \
  --name grafana \
  -p 3000:3000 \
  # Конфигурация базы данных для хранения метаданных
  -e GF_DATABASE_TYPE=postgres \
  -e GF_DATABASE_HOST=postgres:5432 \
  -e GF_DATABASE_NAME=grafana \
  -e GF_DATABASE_USER=grafana \
  -e GF_DATABASE_PASSWORD=secure_password_123 \
  # SSL для защищенного соединения
  -e GF_DATABASE_SSL_MODE=require \
  # Настройки пула соединений
  -e GF_DATABASE_MAX_OPEN_CONN=100 \
  -e GF_DATABASE_MAX_IDLE_CONN=10 \
  # Таймауты для соединений
  -e GF_DATABASE_CONN_MAX_LIFETIME=14400 \
  grafana/grafana:latest
```

**Docker Compose для полной инфраструктуры:**
```yaml
version: '3.8'
services:
  # PostgreSQL для хранения Grafana метаданных
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: grafana
      POSTGRES_USER: grafana
      POSTGRES_PASSWORD: secure_password_123
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - monitoring

  # Grafana с полной конфигурацией
  grafana:
    image: grafana/grafana:latest
    depends_on:
      - postgres
    ports:
      - "3000:3000"
    environment:
      # База данных
      GF_DATABASE_TYPE: postgres
      GF_DATABASE_HOST: postgres:5432
      GF_DATABASE_NAME: grafana
      GF_DATABASE_USER: grafana
      GF_DATABASE_PASSWORD: secure_password_123

      # Безопасность
      GF_SECURITY_ADMIN_PASSWORD: very_strong_admin_password
      GF_SECURITY_SECRET_KEY: your_secret_key_here

      # SMTP для алертинга
      GF_SMTP_ENABLED: true
      GF_SMTP_HOST: smtp.gmail.com:587
      GF_SMTP_USER: your-email@gmail.com
      GF_SMTP_PASSWORD: your-app-password

      # Установка плагинов при запуске
      GF_INSTALL_PLUGINS: grafana-piechart-panel,grafana-clock-panel,grafana-worldmap-panel

      # Настройки сессий
      GF_SESSION_PROVIDER: database
      GF_SESSION_PROVIDER_CONFIG: "host=postgres user=grafana password=secure_password_123 dbname=grafana"

      # Настройки логирования
      GF_LOG_LEVEL: info
      GF_LOG_MODE: console,file

      # Анонимный доступ (для публичных dashboards)
      GF_AUTH_ANONYMOUS_ENABLED: true
      GF_AUTH_ANONYMOUS_ORG_ROLE: Viewer

    volumes:
      # Persistent storage для dashboards и конфигураций
      - grafana_data:/var/lib/grafana
      # Кастомные provisioning файлы
      - ./provisioning:/etc/grafana/provisioning
      # Кастомные dashboards
      - ./dashboards:/var/lib/grafana/dashboards

    networks:
      - monitoring

    # Health check для проверки готовности
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:3000/api/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

volumes:
  grafana_data:
  postgres_data:

networks:
  monitoring:
    driver: bridge
```

#### Kubernetes установка

**Kubernetes** предоставляет декларативный способ развертывания Grafana в production средах с автоматическим масштабированием и self-healing.

```yaml
# grafana-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: monitoring
  labels:
    app: grafana
    component: monitoring
spec:
  # Реплики для high availability
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
        component: monitoring
    spec:
      # Security context
      securityContext:
        runAsUser: 472
        runAsGroup: 472
        fsGroup: 472

      containers:
      - name: grafana
        image: grafana/grafana:latest
        imagePullPolicy: IfNotPresent

        ports:
        - name: http
          containerPort: 3000
          protocol: TCP

        # Переменные окружения
        env:
        - name: GF_SECURITY_ADMIN_PASSWORD
          valueFrom:
            secretKeyRef:
              name: grafana-secret
              key: admin-password

        - name: GF_DATABASE_TYPE
          value: "postgres"

        - name: GF_DATABASE_HOST
          value: "grafana-postgres"

        - name: GF_DATABASE_NAME
          valueFrom:
            configMapKeyRef:
              name: grafana-config
              key: database-name

        - name: GF_DATABASE_USER
          valueFrom:
            secretKeyRef:
              name: grafana-secret
              key: database-user

        - name: GF_DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: grafana-secret
              key: database-password

        # Ресурсные лимиты
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"

        # Liveness probe
        livenessProbe:
          httpGet:
            path: /api/health
            port: 3000
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3

        # Readiness probe
        readinessProbe:
          httpGet:
            path: /api/health
            port: 3000
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3

        # Volume mounts
        volumeMounts:
        - name: storage
          mountPath: /var/lib/grafana
        - name: config
          mountPath: /etc/grafana
        - name: dashboards
          mountPath: /var/lib/grafana/dashboards
        - name: datasources
          mountPath: /etc/grafana/provisioning/datasources
        - name: plugins
          mountPath: /var/lib/grafana/plugins

      volumes:
      - name: storage
        persistentVolumeClaim:
          claimName: grafana-pvc
      - name: config
        configMap:
          name: grafana-config
      - name: dashboards
        configMap:
          name: grafana-dashboards
      - name: datasources
        configMap:
          name: grafana-datasources
      - name: plugins
        emptyDir: {}

---
# Service для доступа к Grafana
apiVersion: v1
kind: Service
metadata:
  name: grafana
  namespace: monitoring
  labels:
    app: grafana
spec:
  type: ClusterIP
  ports:
  - name: http
    port: 3000
    targetPort: 3000
    protocol: TCP
  selector:
    app: grafana

---
# Ingress для внешнего доступа
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: grafana-ingress
  namespace: monitoring
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - grafana.example.com
    secretName: grafana-tls
  rules:
  - host: grafana.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: grafana
            port:
              number: 3000
```

#### Persistent Volume для хранения данных
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: grafana-pvc
  namespace: monitoring
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: fast-ssd
```

#### Установка из бинарных файлов

**Для случаев, когда Docker недоступен или требуется специфическая конфигурация:**

```bash
# Скачиваем и распаковываем
wget https://dl.grafana.com/oss/release/grafana-10.0.0.linux-amd64.tar.gz
tar -zxvf grafana-10.0.0.linux-amd64.tar.gz
cd grafana-10.0.0

# Запускаем
./bin/grafana-server web

# Или как systemd service
sudo cp ./bin/grafana-server /usr/local/bin/
sudo cp ./conf/defaults.ini /etc/grafana/grafana.ini
```

#### Установка через пакеты

**Ubuntu/Debian:**
```bash
# Добавляем репозиторий
wget -q -O - https://packages.grafana.com/gpg.key | sudo apt-key add -
echo "deb https://packages.grafana.com/oss/deb stable main" | sudo tee /etc/apt/sources.list.d/grafana.list

# Устанавливаем
sudo apt update
sudo apt install grafana

# Запускаем
sudo systemctl start grafana
sudo systemctl enable grafana
```

**CentOS/RHEL:**
```bash
# Добавляем репозиторий
cat > /etc/yum.repos.d/grafana.repo << EOF
[grafana]
name=grafana
baseurl=https://packages.grafana.com/oss/rpm
repo_gpgcheck=1
enabled=1
gpgcheck=1
gpgkey=https://packages.grafana.com/gpg.key
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
EOF

# Устанавливаем
sudo yum install grafana

# Запускаем
sudo systemctl start grafana
sudo systemctl enable grafana
```

### Базовая конфигурация

#### grafana.ini
```ini
[server]
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

[auth.anonymous]
enabled = true
org_role = Viewer

[alerting]
enabled = true
execute_alerts = true

[metrics]
enabled = true
basic_auth_username = admin
basic_auth_password = admin
```

#### Environment variables
```bash
# Database configuration
GF_DATABASE_TYPE=postgres
GF_DATABASE_HOST=localhost:5432
GF_DATABASE_NAME=grafana
GF_DATABASE_USER=grafana
GF_DATABASE_PASSWORD=password

# Security
GF_SECURITY_ADMIN_PASSWORD=strongpassword
GF_SECURITY_SECRET_KEY=your-secret-key

# SMTP for alerts
GF_SMTP_ENABLED=true
GF_SMTP_HOST=smtp.gmail.com:587
GF_SMTP_USER=your-email@gmail.com
GF_SMTP_PASSWORD=your-app-password

# Plugins
GF_INSTALL_PLUGINS=grafana-piechart-panel,grafana-clock-panel
```

## Источники данных

### Prometheus Data Source

#### Настройка Prometheus
```json
{
  "name": "Prometheus",
  "type": "prometheus",
  "url": "http://prometheus:9090",
  "access": "proxy",
  "basicAuth": false,
  "jsonData": {
    "timeInterval": "15s",
    "queryTimeout": "60s",
    "httpMethod": "POST"
  }
}
```

#### Тестирование подключения
```bash
# Проверить доступность
curl http://grafana:3000/api/datasources

# Тестовый запрос
curl -X POST \
  http://grafana:3000/api/ds/query \
  -H "Content-Type: application/json" \
  -d '{
    "queries": [{
      "expr": "up",
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus-uid"
      }
    }]
  }'
```

### Multiple Data Sources

#### JSON API Data Source
```json
{
  "name": "JSON API",
  "type": "marcusolsson-json-datasource",
  "url": "http://api.example.com",
  "access": "proxy",
  "jsonData": {
    "defaultPath": "/metrics"
  }
}
```

#### InfluxDB Data Source
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
  }
}
```

### Data Source Permissions

#### Organization permissions
```sql
-- PostgreSQL/Grafana database
INSERT INTO data_source (org_id, name, type, url, access, json_data, created, updated)
VALUES (1, 'Prometheus', 'prometheus', 'http://prometheus:9090', 'proxy', 
        '{"timeInterval":"15s"}', NOW(), NOW());
```

## Создание dashboards

### Dashboard JSON структура

#### Основная структура
```json
{
  "dashboard": {
    "id": null,
    "title": "JVM Metrics Dashboard",
    "tags": ["jvm", "java", "metrics"],
    "timezone": "browser",
    "panels": [],
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "timepicker": {},
    "templating": {
      "list": []
    },
    "annotations": {
      "list": []
    },
    "refresh": "5s",
    "schemaVersion": 27,
    "version": 0,
    "links": []
  }
}
```

### Programmatic dashboard creation

#### Java client для Grafana
```java
public class GrafanaDashboardCreator {

    private final String grafanaUrl;
    private final String apiKey;

    public GrafanaDashboardCreator(String grafanaUrl, String apiKey) {
        this.grafanaUrl = grafanaUrl;
        this.apiKey = apiKey;
    }

    public void createJvmDashboard() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        // Создание dashboard JSON
        Map<String, Object> dashboard = createDashboardJson();
        
        // Отправка в Grafana
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(grafanaUrl + "/api/dashboards/db"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dashboard)))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            System.out.println("Dashboard created successfully");
        } else {
            System.err.println("Failed to create dashboard: " + response.body());
        }
    }

    private Map<String, Object> createDashboardJson() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("id", null);
        dashboard.put("title", "JVM Metrics Dashboard");
        dashboard.put("tags", Arrays.asList("jvm", "java", "metrics"));
        
        // Панели
        List<Map<String, Object>> panels = new ArrayList<>();
        panels.add(createMemoryPanel());
        panels.add(createCpuPanel());
        panels.add(createGcPanel());
        
        dashboard.put("panels", panels);
        
        // Обертка для API
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("dashboard", dashboard);
        wrapper.put("overwrite", false);
        
        return wrapper;
    }

    private Map<String, Object> createMemoryPanel() {
        Map<String, Object> panel = new HashMap<>();
        panel.put("id", 1);
        panel.put("title", "JVM Memory Usage");
        panel.put("type", "graph");
        panel.put("gridPos", Map.of("h", 8, "w", 12, "x", 0, "y", 0));
        
        // Targets
        List<Map<String, Object>> targets = new ArrayList<>();
        Map<String, Object> target = new HashMap<>();
        target.put("expr", "jvm_memory_used_bytes{area=\"heap\"} / jvm_memory_max_bytes{area=\"heap\"}");
        target.put("legendFormat", "Heap Usage %");
        targets.add(target);
        
        panel.put("targets", targets);
        
        return panel;
    }
}
```

## Панели визуализации

### Graph Panel

#### Time series графики
```json
{
  "id": 1,
  "title": "HTTP Request Rate",
  "type": "graph",
  "targets": [{
    "expr": "rate(http_requests_total[5m])",
    "legendFormat": "{{method}} {{endpoint}}",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "reqps",
      "color": {
        "mode": "palette-classic"
      }
    }
  },
  "options": {
    "tooltip": {
      "mode": "multi"
    },
    "legend": {
      "displayMode": "table",
      "placement": "bottom"
    }
  }
}
```

### Table Panel

#### Табличные данные
```json
{
  "id": 2,
  "title": "Top Endpoints",
  "type": "table",
  "targets": [{
    "expr": "topk(10, rate(http_requests_total[5m]))",
    "legendFormat": "{{endpoint}}",
    "refId": "A"
  }],
  "fieldConfig": {
    "overrides": [{
      "matcher": {
        "id": "byName",
        "options": "Time"
      },
      "properties": [{
        "id": "displayName",
        "value": "Timestamp"
      }]
    }]
  },
  "options": {
    "showHeader": true,
    "sortBy": [{
      "desc": true,
      "displayName": "Value"
    }]
  }
}
```

### Stat Panel

#### Ключевые метрики
```json
{
  "id": 3,
  "title": "Error Rate",
  "type": "stat",
  "targets": [{
    "expr": "rate(http_requests_total{status=~\"5..\"}[5m]) / rate(http_requests_total[5m]) * 100",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "orange", "value": 1 },
          { "color": "red", "value": 5 }
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

#### Распределение значений
```json
{
  "id": 4,
  "title": "Response Time Distribution",
  "type": "heatmap",
  "targets": [{
    "expr": "rate(http_request_duration_seconds_bucket[5m])",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "s"
    }
  },
  "options": {
    "calculate": true,
    "calculation": {
      "xBuckets": {
        "mode": "size",
        "value": "50"
      },
      "yBuckets": {
        "mode": "size",
        "value": "50"
      }
    }
  }
}
```

## Spring Boot интеграция

### Spring Boot Admin

#### Настройка Spring Boot Admin
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.7.2</version>
</dependency>
```

```java
@Configuration
@EnableAdminServer
public class AdminServerConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                .pathMatchers("/assets/**").permitAll()
                .anyExchange().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
            .build();
    }
}
```

#### Client приложение
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.7.2</version>
</dependency>
```

```yaml
spring:
  boot:
    admin:
      client:
        url: http://admin-server:8080
        username: admin
        password: admin
        instance:
          metadata:
            tags:
              environment: production
              region: us-east-1

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: when-authorized
```

### Custom metrics в Spring Boot Admin

#### Custom Health Indicator
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            return Health.up()
                .withDetail("database", metaData.getDatabaseProductName())
                .withDetail("version", metaData.getDatabaseProductVersion())
                .withDetail("driver", metaData.getDriverName())
                .build();
                
        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withException(e)
                .build();
        }
    }
}
```

#### Custom metrics endpoint
```java
@RestController
public class CustomMetricsController {

    @Autowired
    private MeterRegistry registry;

    @GetMapping("/metrics/business")
    public Map<String, Object> getBusinessMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Получение метрик из registry
        registry.forEachMeter(meter -> {
            if (meter.getId().getName().startsWith("business")) {
                meter.measure().forEach(measurement -> {
                    metrics.put(meter.getId().getName(), measurement.getValue());
                });
            }
        });
        
        return metrics;
    }
}
```

## JVM monitoring dashboard

### Memory monitoring

#### Heap usage панель
```json
{
  "id": 1,
  "title": "JVM Heap Memory",
  "type": "graph",
  "targets": [
    {
      "expr": "jvm_memory_used_bytes{area=\"heap\"}",
      "legendFormat": "Used",
      "refId": "A"
    },
    {
      "expr": "jvm_memory_committed_bytes{area=\"heap\"}",
      "legendFormat": "Committed",
      "refId": "B"
    },
    {
      "expr": "jvm_memory_max_bytes{area=\"heap\"}",
      "legendFormat": "Max",
      "refId": "C"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "bytes",
      "color": {
        "mode": "palette-classic"
      }
    }
  }
}
```

#### Memory pools
```json
{
  "id": 2,
  "title": "Memory Pools",
  "type": "graph",
  "targets": [
    {
      "expr": "jvm_memory_pool_used_bytes{pool=\"Eden Space\"}",
      "legendFormat": "Eden",
      "refId": "A"
    },
    {
      "expr": "jvm_memory_pool_used_bytes{pool=\"Survivor Space\"}",
      "legendFormat": "Survivor",
      "refId": "B"
    },
    {
      "expr": "jvm_memory_pool_used_bytes{pool=\"Old Gen\"}",
      "legendFormat": "Old Gen",
      "refId": "C"
    }
  ]
}
```

### Garbage Collection

#### GC паузы
```json
{
  "id": 3,
  "title": "GC Pause Time",
  "type": "graph",
  "targets": [{
    "expr": "rate(jvm_gc_pause_seconds_sum[5m])",
    "legendFormat": "{{action}}",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "s"
    }
  }
}
```

#### GC счетчики
```json
{
  "id": 4,
  "title": "GC Collections",
  "type": "stat",
  "targets": [{
    "expr": "increase(jvm_gc_collection_seconds_count[5m])",
    "refId": "A"
  }],
  "options": {
    "reduceOptions": {
      "calcs": ["lastNotNull"]
    }
  }
}
```

### Threads monitoring

#### Thread pools
```json
{
  "id": 5,
  "title": "Thread Pools",
  "type": "graph",
  "targets": [
    {
      "expr": "jvm_threads_live",
      "legendFormat": "Live Threads",
      "refId": "A"
    },
    {
      "expr": "jvm_threads_daemon",
      "legendFormat": "Daemon Threads",
      "refId": "B"
    },
    {
      "expr": "jvm_threads_peak",
      "legendFormat": "Peak Threads",
      "refId": "C"
    }
  ]
}
```

## Application metrics dashboard

### HTTP metrics

#### Request rate
```json
{
  "id": 6,
  "title": "HTTP Request Rate",
  "type": "graph",
  "targets": [{
    "expr": "rate(http_server_requests_seconds_count[5m])",
    "legendFormat": "{{method}} {{uri}}",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "reqps"
    }
  }
}
```

#### Response time percentiles
```json
{
  "id": 7,
  "title": "Response Time Percentiles",
  "type": "graph",
  "targets": [
    {
      "expr": "histogram_quantile(0.5, rate(http_server_requests_seconds_bucket[5m]))",
      "legendFormat": "50th percentile",
      "refId": "A"
    },
    {
      "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))",
      "legendFormat": "95th percentile",
      "refId": "B"
    },
    {
      "expr": "histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))",
      "legendFormat": "99th percentile",
      "refId": "C"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "s"
    }
  }
}
```

### Error rates

#### HTTP error rate
```json
{
  "id": 8,
  "title": "HTTP Error Rate",
  "type": "stat",
  "targets": [{
    "expr": "(rate(http_server_requests_seconds_count{status=~\"5..\"}[5m]) / rate(http_server_requests_seconds_count[5m])) * 100",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "thresholds": {
        "steps": [
          { "color": "green", "value": null },
          { "color": "orange", "value": 1 },
          { "color": "red", "value": 5 }
        ]
      }
    }
  }
}
```

### Database metrics

#### Connection pool
```json
{
  "id": 9,
  "title": "Database Connections",
  "type": "graph",
  "targets": [
    {
      "expr": "hikaricp_connections_active",
      "legendFormat": "Active",
      "refId": "A"
    },
    {
      "expr": "hikaricp_connections_idle",
      "legendFormat": "Idle",
      "refId": "B"
    },
    {
      "expr": "hikaricp_connections_pending",
      "legendFormat": "Pending",
      "refId": "C"
    }
  ]
}
```

#### Query performance
```json
{
  "id": 10,
  "title": "Database Query Performance",
  "type": "table",
  "targets": [{
    "expr": "rate(jdbc_queries_seconds_sum[5m]) / rate(jdbc_queries_seconds_count[5m])",
    "legendFormat": "{{query}}",
    "refId": "A"
  }],
  "fieldConfig": {
    "overrides": [{
      "matcher": {
        "id": "byName",
        "options": "Value"
      },
      "properties": [{
        "id": "displayName",
        "value": "Avg Query Time (s)"
      }]
    }]
  }
}
```

## Business metrics dashboard

### User engagement

#### User registrations
```json
{
  "id": 11,
  "title": "User Registrations",
  "type": "graph",
  "targets": [{
    "expr": "rate(user_registrations_total[5m])",
    "legendFormat": "Registrations per second",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "reqps"
    }
  }
}
```

#### Active users
```json
{
  "id": 12,
  "title": "Active Users",
  "type": "stat",
  "targets": [{
    "expr": "active_users",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "none"
    }
  }
}
```

### Business transactions

#### Order processing
```json
{
  "id": 13,
  "title": "Order Processing",
  "type": "graph",
  "targets": [
    {
      "expr": "rate(orders_created_total[5m])",
      "legendFormat": "Orders Created",
      "refId": "A"
    },
    {
      "expr": "rate(orders_completed_total[5m])",
      "legendFormat": "Orders Completed",
      "refId": "B"
    }
  ]
}
```

#### Revenue metrics
```json
{
  "id": 14,
  "title": "Revenue Metrics",
  "type": "stat",
  "targets": [{
    "expr": "revenue_total",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "currencyUSD"
    }
  }
}
```

### Service level indicators

#### SLA compliance
```json
{
  "id": 15,
  "title": "SLA Compliance",
  "type": "gauge",
  "targets": [{
    "expr": "(1 - (rate(http_server_requests_seconds_count{status=~\"5..\"}[30d]) / rate(http_server_requests_seconds_count[30d]))) * 100",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "steps": [
          { "color": "red", "value": null },
          { "color": "orange", "value": 99.5 },
          { "color": "green", "value": 99.9 }
        ]
      }
    }
  }
}
```

## Алертинг в Grafana

### Alert rules

#### Создание алерта
```json
{
  "name": "High Error Rate",
  "message": "Error rate is {{ .Value }}% for {{ .Labels.instance }}",
  "conditions": [{
    "evaluator": {
      "type": "gt",
      "params": [5]
    },
    "operator": {
      "type": "and"
    },
    "query": {
      "params": ["A", "5m", "now"]
    },
    "reducer": {
      "type": "avg"
    },
    "type": "query"
  }],
  "executionErrorState": "alerting",
  "frequency": "60s",
  "handler": 1,
  "noDataState": "no_value",
  "notifications": [{
    "id": 1
  }]
}
```

### Notification channels

#### Email notification
```json
{
  "name": "Email Alert",
  "type": "email",
  "settings": {
    "addresses": "alerts@company.com",
    "singleEmail": false
  }
}
```

#### Slack notification
```json
{
  "name": "Slack Alert",
  "type": "slack",
  "settings": {
    "url": "https://hooks.slack.com/services/...",
    "recipient": "#alerts",
    "username": "Grafana Alert",
    "icon_emoji": ":warning:"
  }
}
```

#### Webhook notification
```json
{
  "name": "Webhook Alert",
  "type": "webhook",
  "settings": {
    "url": "http://alert-manager:9093/api/v1/alerts",
    "httpMethod": "POST",
    "username": "",
    "password": ""
  }
}
```

### Alert state management

#### Alert states
```java
public class AlertStateManager {

    @Autowired
    private GrafanaApiClient grafanaClient;

    public void manageAlertState(String alertName, AlertState newState) {
        try {
            // Получение текущего состояния алерта
            Alert currentAlert = grafanaClient.getAlert(alertName);
            
            // Обновление состояния
            Alert updatedAlert = new Alert();
            updatedAlert.setName(alertName);
            updatedAlert.setState(newState);
            
            // Отправка в Grafana
            grafanaClient.updateAlert(updatedAlert);
            
            // Логирование
            logger.info("Alert {} state changed to {}", alertName, newState);
            
        } catch (Exception e) {
            logger.error("Failed to manage alert state for {}", alertName, e);
        }
    }

    public void pauseAlert(String alertName, Duration duration) {
        // Временное отключение алерта
        grafanaClient.pauseAlert(alertName, true);
        
        // Планирование включения
        scheduledExecutor.schedule(() -> {
            grafanaClient.pauseAlert(alertName, false);
            logger.info("Alert {} resumed", alertName);
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
    }
}
```

## Templates и variables

### Dashboard variables

#### Query variable
```json
{
  "name": "service",
  "type": "query",
  "datasource": "Prometheus",
  "query": "label_values(up, service)",
  "refresh": 1,
  "sort": 1,
  "multi": true,
  "includeAll": true,
  "allValue": ".*"
}
```

#### Custom variable
```json
{
  "name": "environment",
  "type": "custom",
  "query": "production,staging,development",
  "current": {
    "value": "production",
    "text": "Production"
  },
  "options": [
    { "value": "production", "text": "Production" },
    { "value": "staging", "text": "Staging" },
    { "value": "development", "text": "Development" }
  ]
}
```

### Template queries

#### Переменные в запросах
```promql
# Использование переменных в PromQL
up{service="$service", environment="$environment"}

# HTTP метрики для выбранного сервиса
rate(http_requests_total{service="$service"}[5m])

# Сравнение средних значений
rate(http_requests_total{service="$service"}[5m]) / avg(rate(http_requests_total[5m]))
```

### Dynamic dashboards

#### Multi-environment dashboard
```json
{
  "title": "Multi-Environment Overview",
  "templating": {
    "list": [
      {
        "name": "environment",
        "type": "query",
        "query": "label_values(up, environment)",
        "refresh": 1
      },
      {
        "name": "service",
        "type": "query",
        "query": "label_values(up{environment=\"$environment\"}, service)",
        "refresh": 2
      }
    ]
  },
  "panels": [
    {
      "title": "Service Health - $service ($environment)",
      "targets": [{
        "expr": "up{environment=\"$environment\", service=\"$service\"}",
        "legendFormat": "$service"
      }]
    }
  ]
}
```

## Permissions и security

### User roles

#### Organization roles
- **Viewer** — только просмотр dashboards
- **Editor** — создание и редактирование dashboards
- **Admin** — полный доступ к организации

#### Dashboard permissions
```json
{
  "dashboardId": 1,
  "permissions": [
    {
      "teamId": 1,
      "permission": 1  // View
    },
    {
      "userId": 2,
      "permission": 2  // Edit
    },
    {
      "role": "Admin",
      "permission": 4  // Admin
    }
  ]
}
```

### API keys

#### Service account tokens
```bash
# Создание service account
curl -X POST \
  http://grafana:3000/api/serviceaccounts \
  -H "Authorization: Bearer $API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "monitoring-service",
    "role": "Editor"
  }'

# Создание token
curl -X POST \
  http://grafana:3000/api/serviceaccounts/1/tokens \
  -H "Authorization: Bearer $API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "api-token"
  }'
```

### Secure configuration

#### Environment-based config
```yaml
# application.yml
grafana:
  url: ${GRAFANA_URL:http://localhost:3000}
  api-key: ${GRAFANA_API_KEY}
  dashboards:
    - name: jvm-metrics
      file: classpath:/grafana/jvm-dashboard.json
    - name: app-metrics
      file: classpath:/grafana/app-dashboard.json
```

## Performance optimization

### Dashboard optimization

#### Query optimization
```json
{
  "targets": [{
    "expr": "rate(http_requests_total[5m])",
    "interval": "30s",  // Интервал запроса
    "intervalFactor": 2,
    "step": 30,
    "refId": "A"
  }],
  "timeFrom": "1h",     // Ограничение временного диапазона
  "maxDataPoints": 1000 // Максимальное количество точек
}
```

#### Caching
```yaml
# Grafana configuration
[cache]
enabled = true
ttl = 3600  # 1 hour

[query_cache]
enabled = true
ttl = 300   # 5 minutes
```

### Database optimization

#### PostgreSQL tuning
```sql
-- Оптимизация для Grafana
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET work_mem = '4MB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';

-- Индексы для метрик
CREATE INDEX CONCURRENTLY idx_dashboard_tag ON dashboard_tag (term);
CREATE INDEX CONCURRENTLY idx_annotation ON annotation (dashboard_id, epoch);
```

### Frontend optimization

#### Panel limits
```json
{
  "options": {
    "maxDataPoints": 500,  // Ограничение точек данных
    "interval": "30s"       // Интервал обновления
  }
}
```

## Best practices

### 1. Dashboard organization

#### Naming conventions
```
[Environment] Service Name - Metric Type
[Prod] User Service - HTTP Metrics
[Staging] Payment API - Database Performance
[Dev] Order Service - Business KPIs
```

#### Folder structure
```
📁 Dashboards/
├── 📁 Infrastructure/
│   ├── System Metrics
│   ├── Network Monitoring
│   └── Database Performance
├── 📁 Applications/
│   ├── User Service
│   ├── Payment Service
│   └── Order Service
├── 📁 Business/
│   ├── Revenue Dashboard
│   ├── User Analytics
│   └── SLA Monitoring
└── 📁 Alerts/
    ├── Critical Alerts
    └── Warning Alerts
```

### 2. Panel design

#### Consistent styling
```json
{
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "decimals": 2,
      "color": {
        "mode": "thresholds"
      },
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "orange", "value": 80 },
          { "color": "red", "value": 90 }
        ]
      }
    }
  }
}
```

#### Information hierarchy
- **Primary metrics** — крупные stat panels сверху
- **Trends** — графики в центре
- **Details** — таблицы и drill-down внизу
- **Context** — annotations и events

### 3. Alert management

#### Alert naming
```
[Severity] Service - Issue - Location
[CRITICAL] Payment Service - High Error Rate - API Gateway
[WARNING] User Service - Slow Response - Authentication
[INFO] Database - Connection Pool Usage - Primary DB
```

#### Alert escalation
```json
{
  "name": "Escalating Alert",
  "conditions": [
    {
      "evaluator": { "type": "gt", "params": [5] },
      "query": { "params": ["A", "5m", "now"] }
    }
  ],
  "notifications": [
    { "id": 1 },  // Email
    { "id": 2, "sendReminder": true, "frequency": "15m" }  // Slack with reminder
  ]
}
```

### 4. Template usage

#### Reusable templates
```json
{
  "templating": {
    "list": [
      {
        "name": "datasource",
        "type": "datasource",
        "query": "prometheus",
        "current": { "value": "Prometheus", "text": "Prometheus" }
      },
      {
        "name": "service",
        "type": "query",
        "datasource": "$datasource",
        "query": "label_values(up, service)",
        "refresh": 1,
        "multi": true
      }
    ]
  }
}
```

### 5. Security best practices

#### API key management
```java
@Configuration
public class GrafanaSecurityConfig {

    @Bean
    public GrafanaApiKeyRotator apiKeyRotator() {
        return new GrafanaApiKeyRotator();
    }

    @Scheduled(fixedRate = 86400000) // Daily rotation
    public void rotateApiKeys() {
        try {
            // Создание нового ключа
            String newKey = grafanaClient.createApiKey("automated-key", "Admin");
            
            // Обновление конфигурации
            updateConfiguration(newKey);
            
            // Удаление старых ключей
            cleanupOldKeys();
            
            logger.info("Grafana API key rotated successfully");
            
        } catch (Exception e) {
            logger.error("Failed to rotate Grafana API key", e);
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Data source connection issues
```bash
# Проверка доступности data source
curl http://grafana:3000/api/datasources

# Тест подключения
curl -X POST \
  http://grafana:3000/api/datasources/1/health \
  -H "Authorization: Bearer $API_KEY"

# Логи Grafana
docker logs grafana

# Проверка Prometheus
curl http://prometheus:9090/api/v1/query?query=up
```

#### Slow dashboard loading
```bash
# Оптимизация запросов
# 1. Уменьшить интервал обновления
# 2. Использовать larger step sizes
# 3. Limit time ranges
# 4. Use summary metrics instead of detailed

# Пример оптимизации
curl -X PATCH \
  http://grafana:3000/api/dashboards/db/dashboard-name \
  -H "Authorization: Bearer $API_KEY" \
  -d '{
    "dashboard": {
      "refresh": "1m",
      "time": { "from": "now-1h", "to": "now" }
    }
  }'
```

#### Permission issues
```bash
# Проверить permissions
curl http://grafana:3000/api/user

# Проверить organization
curl http://grafana:3000/api/user/orgs

# Проверить dashboard permissions
curl http://grafana:3000/api/dashboards/id/1/permissions
```

### Debug techniques

#### Query debugging
```bash
# Отладка PromQL запросов
curl "http://grafana:3000/api/ds/query" \
  -H "Authorization: Bearer $API_KEY" \
  -d '{
    "queries": [{
      "expr": "rate(http_requests_total[5m])",
      "datasource": { "type": "prometheus" }
    }]
  }'

# Проверка query performance
curl "http://prometheus:9090/api/v1/query_range?query=rate(http_requests_total[5m])&start=1609459200&end=1609462800&step=60"
```

#### Dashboard validation
```bash
# Валидация dashboard JSON
curl -X POST \
  http://grafana:3000/api/dashboards/db \
  -H "Authorization: Bearer $API_KEY" \
  -H "Content-Type: application/json" \
  -d @dashboard.json

# Получение dashboard
curl http://grafana:3000/api/dashboards/db/dashboard-name

# Поиск dashboard
curl "http://grafana:3000/api/search?query=HTTP&type=dash-db"
```

### Performance monitoring

#### Grafana metrics
```promql
# Grafana internal metrics
grafana_stat_totals_dashboard
grafana_stat_totals_datasource

# Query performance
rate(grafana_api_response_duration_seconds_sum[5m]) / rate(grafana_api_response_duration_seconds_count[5m])

# Dashboard usage
grafana_dashboard_totals
```

#### System monitoring
```bash
# Мониторинг Grafana процесса
ps aux | grep grafana

# Проверка использования ресурсов
docker stats grafana

# Database performance
docker exec -it grafana-db psql -c "SELECT * FROM pg_stat_activity;"

# Логи производительности
tail -f /var/log/grafana/grafana.log | grep -i error
```

## Заключение

**Grafana** — это мощная платформа для создания красивых и информативных dashboards, которая отлично интегрируется с Prometheus и другими источниками данных. Для Java-приложений Grafana предоставляет все необходимые инструменты для мониторинга JVM метрик, application метрик и business KPIs.

### Ключевые возможности:

1. **Источники данных** — поддержка 50+ источников, включая Prometheus
2. **Визуализации** — богатый набор панелей для различных типов данных
3. **Templates и variables** — динамические и переиспользуемые dashboards
4. **Алертинг** — интегрированная система оповещений
5. **Права доступа** — granular управление доступом
6. **Performance** — оптимизация для высоконагруженных систем

### Архитектурные преимущества:

#### Flexibility:
- **Multiple data sources** — одновременная работа с разными источниками
- **Custom panels** — расширяемость через плагины
- **API-driven** — программное управление dashboards
- **Multi-tenant** — поддержка нескольких организаций

#### User Experience:
- **Interactive dashboards** — drill-down и exploration
- **Real-time updates** — живые данные
- **Mobile support** — адаптивный интерфейс
- **Sharing** — экспорт и sharing dashboards

### Когда использовать Grafana:

✅ **Мониторинг приложений** — JVM, application и business метрики
✅ **Визуализация данных** — dashboards для различных stakeholders
✅ **Алертинг** — интегрированные оповещения
✅ **Multi-source monitoring** — работа с несколькими источниками данных
✅ **Team collaboration** — sharing dashboards в команде
✅ **Business intelligence** — отчеты и аналитика
✅ **Real-time monitoring** — live dashboards
✅ **Historical analysis** — анализ трендов и аномалий

### Когда НЕ использовать:

❌ **Simple logging** — для базового логирования без визуализации
❌ **Single metric monitoring** — для одного типа метрик
❌ **Real-time trading** — для ultra-low latency требований
❌ **Static reports** — для фиксированных отчетов без обновлений
❌ **Resource constraints** — ограниченные ресурсы для dashboard сервера
❌ **Basic users** — для пользователей без технических навыков

### Best practices:

1. **Организация dashboards** — логическая структура и naming conventions
2. **Оптимизация производительности** — caching и query optimization
3. **Безопасность** — API keys и permissions management
4. **Templates** — reusable components и variables
5. **Алертинг** — escalation и notification management
6. **Monitoring** — метрики самой Grafana
7. **Version control** — dashboards as code
8. **Documentation** — описание dashboards и метрик

### Интеграция с другими инструментами:

- **Prometheus** — основной источник метрик
- **Alertmanager** — маршрутизация алертов
- **Loki** — логи aggregation
- **Jaeger/Zipkin** — distributed tracing
- **Elasticsearch** — advanced search и analytics
- **InfluxDB** — time-series database
- **Kubernetes** — container orchestration
- **Spring Boot Admin** — Java application management

### Dashboard lifecycle:

1. **Design** — проектирование структуры и layout
2. **Implementation** — создание panels и queries
3. **Testing** — валидация данных и performance
4. **Deployment** — развертывание в production
5. **Maintenance** — обновление и оптимизация
6. **Versioning** — управление версиями dashboards

Grafana является стандартом де-факто для визуализации метрик в современной экосистеме мониторинга. Правильная настройка и использование Grafana обеспечивает глубокое понимание работы приложений и быструю реакцию на проблемы. 🚀

**Далее: Jaeger для distributed tracing**