---
title: "Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh"
description: "Полное руководство по cloud-native функциям Quarkus: Kubernetes, OpenShift, service mesh, distributed tracing и best practices"
tags:
  - quarkus
  - kubernetes
  - openshift
  - cloud-native
  - service-mesh
  - java
type: "overview"
difficulty: "intermediate"
aliases:
  - "Quarkus"
  - "quarkus cloud"
prerequisites:
  - "[[quarkus-basics]]"
  - "[[quarkus-core]]"
related:
  - "[[quarkus-core]]"
  - "[[quarkus-graalvm]]"
next:
  - "[[quarkus-core]]"
  - "[[quarkus-graalvm]]"
updated: "2026-04-20"
---

# Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Kubernetes](#kubernetes)
  - [Kubernetes Deployment](#kubernetes-deployment)
  - [Kubernetes Configuration](#kubernetes-configuration)
- [OpenShift](#openshift)
  - [OpenShift Configuration](#openshift-configuration)
- [Service Mesh](#service-mesh)
  - [Istio Integration](#istio-integration)
- [Distributed Tracing](#distributed-tracing)
  - [OpenTelemetry Configuration](#opentelemetry-configuration)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте health checks](#1-используйте-health-checks)
  - [2. Настройте metrics](#2-настройте-metrics)
  - [3. Используйте distributed tracing](#3-используйте-distributed-tracing)
- [Kubernetes Deployment Strategies](#kubernetes-deployment-strategies)
  - [Rolling Update](#rolling-update)
  - [Blue-Green Deployment](#blue-green-deployment)
  - [Canary Deployment](#canary-deployment)
- [Health Checks](#health-checks)
  - [Liveness и Readiness Probes](#liveness-и-readiness-probes)
  - [Custom Health Checks](#custom-health-checks)
- [Metrics и Monitoring](#metrics-и-monitoring)
  - [Micrometer Integration](#micrometer-integration)
  - [Custom Metrics](#custom-metrics)
  - [Prometheus ServiceMonitor](#prometheus-servicemonitor)
- [Distributed Tracing](#distributed-tracing-1)
  - [OpenTelemetry Configuration](#opentelemetry-configuration-1)
  - [Custom Spans](#custom-spans)
  - [Jaeger Configuration](#jaeger-configuration)
- [Service Mesh Integration](#service-mesh-integration)
  - [Istio Configuration](#istio-configuration)
  - [Circuit Breaker](#circuit-breaker)
- [ConfigMaps и Secrets](#configmaps-и-secrets)
  - [ConfigMap](#configmap)
  - [Secrets](#secrets)
- [Horizontal Pod Autoscaling](#horizontal-pod-autoscaling)
  - [HPA Configuration](#hpa-configuration)
- [Resource Management](#resource-management)
  - [Resource Limits](#resource-limits)
- [Service Discovery](#service-discovery)
  - [Kubernetes Service Discovery](#kubernetes-service-discovery)
  - [Consul Service Discovery](#consul-service-discovery)
- [Configuration Management](#configuration-management)
  - [External Configuration](#external-configuration)
  - [ConfigMaps и Secrets](#configmaps-и-secrets-1)
- [Advanced Cloud Patterns](#advanced-cloud-patterns)
  - [Blue-Green Deployment](#blue-green-deployment-1)
  - [Canary Deployment](#canary-deployment-1)
  - [Service Mesh Integration](#service-mesh-integration-1)
- [Kubernetes Resource Management](#kubernetes-resource-management)
  - [Resource Limits and Requests](#resource-limits-and-requests)
  - [Pod Disruption Budget](#pod-disruption-budget)
- [Service Mesh Integration](#service-mesh-integration-2)
  - [Circuit Breaker Configuration](#circuit-breaker-configuration)
  - [Retry Policy](#retry-policy)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** оптимизирован для **cloud-native** приложений с полной поддержкой **Kubernetes**, **OpenShift** и **service mesh**. Это позволяет создавать масштабируемые, отказоустойчивые микросервисы.

### Основные возможности

- **Kubernetes**: Полная поддержка **Kubernetes**
- **OpenShift**: Оптимизация для **OpenShift**
- **Service Mesh**: Интеграция с **Istio**, **Linkerd**
- **Distributed Tracing**: **OpenTelemetry**, **Jaeger**

## Kubernetes

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: quarkus-app
  template:
    metadata:
      labels:
        app: quarkus-app
    spec:
      containers:
      - name: quarkus-app
        image: quarkus-app:latest
        ports:
        - containerPort: 8080
```

### Kubernetes Configuration

**application.properties:**

```properties
quarkus.kubernetes.deployment-target=kubernetes
quarkus.kubernetes.replicas=3
quarkus.kubernetes.image-pull-policy=Always
```

## OpenShift

### OpenShift Configuration

**application.properties:**

```properties
quarkus.openshift.deployment-target=openshift
quarkus.openshift.route.expose=true
quarkus.openshift.route.host=myapp.example.com
```

## Service Mesh

### Istio Integration

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: quarkus-app
spec:
  hosts:
  - quarkus-app
  http:
  - route:
    - destination:
        host: quarkus-app
```

## Distributed Tracing

### OpenTelemetry Configuration

**application.properties:**

```properties
quarkus.opentelemetry.enabled=true
quarkus.opentelemetry.tracer.exporter.otlp.endpoint=http://jaeger:4317
```

## Лучшие практики

### 1. Используйте health checks

```properties
# ✅ Хорошо
quarkus.smallrye-health.ui.enable=true
```

### 2. Настройте metrics

```properties
# ✅ Хорошо
quarkus.micrometer.export.prometheus.enabled=true
```

### 3. Используйте distributed tracing

```properties
# ✅ Хорошо
quarkus.opentelemetry.enabled=true
```

## Kubernetes Deployment Strategies

### Rolling Update

**Настройка **rolling update** для безопасного обновления:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      containers:
      - name: quarkus-app
        image: quarkus-app:latest
        readinessProbe:
          httpGet:
            path: /q/health/ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /q/health/live
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Blue-Green Deployment

**Реализация **blue-green deployment**:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: quarkus-app
spec:
  selector:
    version: blue  # Переключение между blue и green
  ports:
  - port: 80
    targetPort: 8080
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-app-blue
spec:
  replicas: 3
  template:
    metadata:
      labels:
        version: blue
    spec:
      containers:
      - name: quarkus-app
        image: quarkus-app:v1
```

### Canary Deployment

**Настройка **canary deployment**:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: quarkus-app
spec:
  hosts:
  - quarkus-app
  http:
  - match:
    - headers:
        canary:
          exact: "true"
    route:
    - destination:
        host: quarkus-app
        subset: canary
      weight: 100
  - route:
    - destination:
        host: quarkus-app
        subset: stable
      weight: 90
    - destination:
        host: quarkus-app
        subset: canary
      weight: 10
```

## Health Checks

### Liveness и Readiness Probes

**Настройка **health checks**:**

```properties
# application.properties
quarkus.smallrye-health.root-path=/health
quarkus.smallrye-health.liveness-path=/live
quarkus.smallrye-health.readiness-path=/ready
quarkus.smallrye-health.startup-path=/started
```

### Custom Health Checks

**Создание кастомных **health checks**:**

```java
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Application")
            .up()
            .withData("status", "alive")
            .build();
    }
}

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        boolean isReady = checkDatabase();
        return HealthCheckResponse.named("Database")
            .status(isReady)
            .withData("database", isReady ? "connected" : "disconnected")
            .build();
    }

    private boolean checkDatabase() {
        try {
            dataSource.getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

## Metrics и Monitoring

### Micrometer Integration

**Настройка **Micrometer** для метрик:**

```properties
# application.properties
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
quarkus.micrometer.export.prometheus.path=/metrics
```

### Custom Metrics

**Создание кастомных метрик:**

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricsService {

    private final Counter requestCounter;
    private final Counter errorCounter;

    @Inject
    public MetricsService(MeterRegistry registry) {
        this.requestCounter = Counter.builder("requests.total")
            .description("Total number of requests")
            .register(registry);

        this.errorCounter = Counter.builder("errors.total")
            .description("Total number of errors")
            .register(registry);
    }

    public void incrementRequest() {
        requestCounter.increment();
    }

    public void incrementError() {
        errorCounter.increment();
    }
}
```

### Prometheus ServiceMonitor

**Настройка **ServiceMonitor** для **Prometheus**:**

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: quarkus-app
spec:
  selector:
    matchLabels:
      app: quarkus-app
  endpoints:
  - port: http
    path: /metrics
    interval: 30s
```

## Distributed Tracing

### OpenTelemetry Configuration

**Настройка **OpenTelemetry**:**

```properties
# application.properties
quarkus.opentelemetry.enabled=true
quarkus.opentelemetry.tracer.exporter.otlp.endpoint=http://jaeger:4317
quarkus.opentelemetry.tracer.exporter.otlp.protocol=grpc
```

### Custom Spans

**Создание кастомных **spans**:**

```java
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users")
public class UserResource {

    @Inject
    Tracer tracer;

    @GET
    @Path("/{id}")
    public User getUser(Long id) {
        Span span = tracer.spanBuilder("getUser")
            .setAttribute("user.id", id)
            .startSpan();

        try {
            return userService.findById(id);
        } finally {
            span.end();
        }
    }
}
```

### Jaeger Configuration

**Настройка **Jaeger** для **distributed tracing**:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger
spec:
  template:
    spec:
      containers:
      - name: jaeger
        image: jaegertracing/all-in-one:latest
        ports:
        - containerPort: 16686  # UI
        - containerPort: 4317   # OTLP gRPC
```

## Service Mesh Integration

### Istio Configuration

**Настройка **Istio** для **service mesh**:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: quarkus-app
spec:
  host: quarkus-app
  trafficPolicy:
    loadBalancer:
      simple: LEAST_CONN
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 10
        http2MaxRequests: 100
        maxRequestsPerConnection: 2
    outlierDetection:
      consecutiveErrors: 3
      interval: 30s
      baseEjectionTime: 30s
```

### Circuit Breaker

**Настройка **circuit breaker**:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: quarkus-app
spec:
  host: quarkus-app
  trafficPolicy:
    connectionPool:
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
    outlierDetection:
      consecutiveErrors: 1
      interval: 1s
      baseEjectionTime: 3m
      maxEjectionPercent: 100
```

## ConfigMaps и Secrets

### ConfigMap

**Использование **ConfigMap**:**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: quarkus-config
data:
  application.properties: |
    quarkus.datasource.jdbc.url=jdbc:postgresql://db:5432/mydb
    quarkus.log.level=INFO
```

**Монтирование **ConfigMap**:**

```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: quarkus-app
        volumeMounts:
        - name: config
          mountPath: /app/config
      volumes:
      - name: config
        configMap:
          name: quarkus-config
```

### Secrets

**Использование **Secrets**:**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: quarkus-secrets
type: Opaque
stringData:
  db-password: mypassword
  api-key: myapikey
```

**Использование в приложении:**

```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: quarkus-app
        env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: quarkus-secrets
              key: db-password
```

## Horizontal Pod Autoscaling

### HPA Configuration

**Настройка **HPA**:**

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: quarkus-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: quarkus-app
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
```

## Resource Management

### Resource Limits

**Настройка **resource limits**:**

```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: quarkus-app
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

## Service Discovery

### Kubernetes Service Discovery

**Обнаружение сервисов в **Kubernetes**:**

```properties
# application.properties
quarkus.kubernetes.service-binding.enabled=true
```

### Consul Service Discovery

**Интеграция с **Consul**:**

```java
import io.quarkus.consul.config.ConsulConfig;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConsulServiceDiscovery {

    @Inject
    ConsulConfig consulConfig;

    public String discoverService(String serviceName) {
        return consulConfig.getServiceUrl(serviceName);
    }
}
```

## Configuration Management

### External Configuration

**Внешняя конфигурация:**

```properties
# application.properties
quarkus.config.locations=file:/etc/myapp/application.properties
quarkus.config.optional=true
```

### ConfigMaps и Secrets

**Использование **ConfigMaps** и **Secrets**:**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  application.properties: |
    app.name=My Application
    app.version=1.0.0
```

## Advanced Cloud Patterns

### Blue-Green Deployment

**Blue-Green** развертывание:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app
spec:
  selector:
    app: my-app
    version: blue
  ports:
    - port: 80
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
      version: blue
  template:
    metadata:
      labels:
        app: my-app
        version: blue
```

### Canary Deployment

**Canary** развертывание:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app
spec:
  selector:
    app: my-app
  ports:
    - port: 80
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-canary
spec:
  replicas: 1  # Меньше реплик для canary
  selector:
    matchLabels:
      app: my-app
      version: canary
```

### Service Mesh Integration

**Интеграция с **service mesh**:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: my-app
spec:
  hosts:
    - my-app
  http:
    - route:
        - destination:
            host: my-app
            subset: v1
          weight: 90
        - destination:
            host: my-app
            subset: v2
          weight: 10
```

## Kubernetes Resource Management

### Resource Limits and Requests

**Ограничения и запросы ресурсов:**

```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```

### Pod Disruption Budget

**Бюджет прерывания подов:**

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: my-app-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: my-app
```

## Service Mesh Integration

### Circuit Breaker Configuration

**Настройка **Circuit Breaker**:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: my-app
spec:
  host: my-app
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 10
        http2MaxRequests: 2
        maxRequestsPerConnection: 1
    circuitBreaker:
      consecutiveErrors: 3
      interval: 30s
      baseEjectionTime: 30s
```

### Retry Policy

**Политика повторных попыток:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: my-app
spec:
  hosts:
    - my-app
  http:
    - retries:
        attempts: 3
        perTryTimeout: 2s
        retryOn: 5xx,reset,connect-failure,refused-stream
```


## Заключение

**Quarkus Cloud Native** предоставляет мощные инструменты для создания **cloud-native** приложений. Поддержка **Kubernetes**, **OpenShift**, **service mesh**, **distributed tracing**, **service discovery**, **configuration management** и других продвинутых возможностей позволяет создавать масштабируемые микросервисы. Правильная настройка **deployment** стратегий, **health checks**, метрик, **distributed tracing**, автоматического масштабирования и **service discovery** являются ключевыми аспектами создания надежных **cloud-native** приложений.

## Дополнительные ресурсы

- [**Quarkus Kubernetes** Guide](https://quarkus.io/guides/kubernetes)
- [**Quarkus OpenShift** Guide](https://quarkus.io/guides/deploying-to-openshift)
- [**Kubernetes** Documentation](https://kubernetes.io/docs/)
- [Istio Documentation](https://istio.io/latest/docs/)
- [OpenTelemetry](https://opentelemetry.io/docs/)
- [Consul Documentation](https://developer.hashicorp.com/consul/docs)

## См. также

- [Quarkus: Actuator — Health Checks и Metrics](quarkus-actuator.md)
- [Quarkus: Основы](quarkus-basics.md)
- [Quarkus: Cache — Кеширование данных](quarkus-cache.md)
- [Quarkus: Core — CDI, Bean Scopes и Configuration](quarkus-core.md)
- [Quarkus: Data Access — Hibernate ORM, Panache и Repositories](quarkus-data.md)
