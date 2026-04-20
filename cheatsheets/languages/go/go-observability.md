---
title: "Go: наблюдаемость"
description: "Полное руководство по наблюдаемости в Go: метрики, трейсинг, логирование, мониторинг"
tags:
  - go
  - golang
  - observability
  - metrics
  - tracing
  - monitoring
difficulty: "advanced"
prerequisites: ["go/go-basics.md", "go/go-logging.md"]
updated: "2026-02-06"
---

# Go: наблюдаемость

## Полезные ссылки

- [Prometheus Go Client](https://github.com/prometheus/client_golang)
- [OpenTelemetry Go](https://opentelemetry.io/docs/go/)

## Содержание

- [Go: наблюдаемость](#go-наблюдаемость)
- [Введение в наблюдаемость](#введение-в-наблюдаемость)
  - [Три столпа наблюдаемости](#три-столпа-наблюдаемости)
- [Метрики](#метрики)
  - [Prometheus метрики](#prometheus-метрики)
  - [Кастомные метрики](#кастомные-метрики)
- [Трейсинг](#трейсинг)
  - [OpenTelemetry трейсинг](#opentelemetry-трейсинг)
  - [Распределенный трейсинг](#распределенный-трейсинг)
- [Логирование](#логирование)
  - [Структурированное логирование](#структурированное-логирование)
  - [Контекстное логирование](#контекстное-логирование)
- [Мониторинг](#мониторинг)
  - [Health checks](#health-checks)
  - [Readiness и Liveness](#readiness-и-liveness)
  - [Детальная настройка Prometheus метрик](#детальная-настройка-prometheus-метрик)
  - [Практические примеры: Middleware для метрик](#практические-примеры-middleware-для-метрик)
  - [Практические примеры: Кастомные метрики](#практические-примеры-кастомные-метрики)
  - [Практические примеры: OpenTelemetry трейсинг](#практические-примеры-opentelemetry-трейсинг)
  - [Практические примеры: Распределенный трейсинг](#практические-примеры-распределенный-трейсинг)
  - [Практические примеры: Структурированное логирование с контекстом](#практические-примеры-структурированное-логирование-с-контекстом)
  - [Практические примеры: Health checks с деталями](#практические-примеры-health-checks-с-деталями)
  - [Практические примеры: Readiness и Liveness probes](#практические-примеры-readiness-и-liveness-probes)
  - [Практические примеры: Метрики для бизнес-логики](#практические-примеры-метрики-для-бизнес-логики)
  - [Практические примеры: Интеграция с Grafana](#практические-примеры-интеграция-с-grafana)
  - [Практические примеры: Health checks](#практические-примеры-health-checks)
  - [Практические примеры: Distributed tracing с OpenTelemetry](#практические-примеры-distributed-tracing-с-opentelemetry)
  - [Практические примеры: Собственные метрики](#практические-примеры-собственные-метрики)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в наблюдаемость

Наблюдаемость — это способность понимать внутреннее состояние системы на основе внешних данных. Go предоставляет инструменты для метрик, трейсинга и логирования.

### Три столпа наблюдаемости

1. **Метрики** — числовые данные о производительности
2. **Логи** — события и сообщения
3. **Трейсы** — запросы через систему

## Метрики

### Prometheus метрики

```go
import (
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
    requestsTotal = prometheus.NewCounterVec(
        prometheus.CounterOpts{
            Name: "http_requests_total",
            Help: "Total number of HTTP requests",
        },
        []string{"method", "status"},
    )

    requestDuration = prometheus.NewHistogramVec(
        prometheus.HistogramOpts{
            Name: "http_request_duration_seconds",
            Help: "HTTP request duration",
        },
        []string{"method"},
    )
)

func init() {
    prometheus.MustRegister(requestsTotal)
    prometheus.MustRegister(requestDuration)
}

func metricsHandler(w http.ResponseWriter, r *http.Request) {
    start := time.Now()

    // Обработка запроса
    processRequest(w, r)

    duration := time.Since(start).Seconds()
    requestsTotal.WithLabelValues(r.Method, "200").Inc()
    requestDuration.WithLabelValues(r.Method).Observe(duration)
}
```

### Кастомные метрики

```go
type CustomMetrics struct {
    requests prometheus.Counter
    errors   prometheus.Counter
    duration prometheus.Histogram
}

func NewCustomMetrics() *CustomMetrics {
    return &CustomMetrics{
        requests: prometheus.NewCounter(prometheus.CounterOpts{
            Name: "custom_requests_total",
        }),
        errors: prometheus.NewCounter(prometheus.CounterOpts{
            Name: "custom_errors_total",
        }),
        duration: prometheus.NewHistogram(prometheus.HistogramOpts{
            Name: "custom_duration_seconds",
        }),
    }
}
```

## Трейсинг

### OpenTelemetry трейсинг

```go
import (
    "go.opentelemetry.io/otel"
    "go.opentelemetry.io/otel/trace"
)

func tracedHandler(w http.ResponseWriter, r *http.Request) {
    ctx := r.Context()
    tracer := otel.Tracer("myapp")

    ctx, span := tracer.Start(ctx, "handleRequest")
    defer span.End()

    // Обработка запроса
    processRequest(ctx, w, r)

    span.SetAttributes(
        attribute.String("method", r.Method),
        attribute.String("path", r.URL.Path),
    )
}
```

### Распределенный трейсинг

```go
func propagateTrace(ctx context.Context, req *http.Request) {
    propagator := otel.GetTextMapPropagator()
    propagator.Inject(ctx, propagation.HeaderCarrier(req.Header))
}
```

## Логирование

### Структурированное логирование

```go
import "log/slog"

func structuredLogging() {
    logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))

    logger.Info("Request processed",
        "method", "GET",
        "path", "/api/users",
        "status", 200,
        "duration_ms", 45,
    )
}
```

### Контекстное логирование

```go
func contextualLogging(ctx context.Context) {
    logger := slog.With(
        "request_id", getRequestID(ctx),
        "user_id", getUserID(ctx),
    )

    logger.Info("Processing request")
}
```

## Мониторинг

### Health checks

```go
func healthCheck(w http.ResponseWriter, r *http.Request) {
    checks := map[string]string{
        "database": checkDatabase(),
        "cache":    checkCache(),
    }

    allHealthy := true
    for _, status := range checks {
        if status != "ok" {
            allHealthy = false
            break
        }
    }

    if allHealthy {
        w.WriteHeader(http.StatusOK)
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
    }

    json.NewEncoder(w).Encode(checks)
}
```

### Readiness и Liveness

```go
func readinessCheck(w http.ResponseWriter, r *http.Request) {
    // Проверка готовности к обработке запросов
    if isReady() {
        w.WriteHeader(http.StatusOK)
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
    }
}

func livenessCheck(w http.ResponseWriter, r *http.Request) {
    // Проверка работоспособности
    w.WriteHeader(http.StatusOK)
}
```

### Детальная настройка Prometheus метрик

```go
import (
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promauto"
    "github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
    // Counter - монотонно возрастающий счетчик
    httpRequestsTotal = promauto.NewCounterVec(
        prometheus.CounterOpts{
            Name: "http_requests_total",
            Help: "Total number of HTTP requests",
        },
        []string{"method", "endpoint", "status"},
    )

    // Gauge - значение, которое может увеличиваться и уменьшаться
    activeConnections = promauto.NewGauge(
        prometheus.GaugeOpts{
            Name: "active_connections",
            Help: "Number of active connections",
        },
    )

    // Histogram - распределение значений
    requestDuration = promauto.NewHistogramVec(
        prometheus.HistogramOpts{
            Name:    "http_request_duration_seconds",
            Help:    "HTTP request duration in seconds",
            Buckets: prometheus.DefBuckets,
        },
        []string{"method", "endpoint"},
    )

    // Summary - похож на Histogram, но с квантилями
    requestSize = promauto.NewSummaryVec(
        prometheus.SummaryOpts{
            Name:       "http_request_size_bytes",
            Help:       "HTTP request size in bytes",
            Objectives: map[float64]float64{0.5: 0.05, 0.9: 0.01, 0.99: 0.001},
        },
        []string{"method"},
    )
)
```

### Практические примеры: Middleware для метрик

```go
func metricsMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()

        // Обертка ResponseWriter для отслеживания статуса
        rw := &responseWriter{ResponseWriter: w, statusCode: http.StatusOK}

        next.ServeHTTP(rw, r)

        duration := time.Since(start).Seconds()
        status := strconv.Itoa(rw.statusCode)

        // Обновление метрик
        httpRequestsTotal.WithLabelValues(r.Method, r.URL.Path, status).Inc()
        requestDuration.WithLabelValues(r.Method, r.URL.Path).Observe(duration)
        requestSize.WithLabelValues(r.Method).Observe(float64(r.ContentLength))
    })
}

type responseWriter struct {
    http.ResponseWriter
    statusCode int
}

func (rw *responseWriter) WriteHeader(code int) {
    rw.statusCode = code
    rw.ResponseWriter.WriteHeader(code)
}
```

### Практические примеры: Кастомные метрики

```go
type BusinessMetrics struct {
    ordersProcessed  prometheus.Counter
    revenue          prometheus.Counter
    averageOrderValue prometheus.Gauge
    processingTime  prometheus.Histogram
}

func NewBusinessMetrics() *BusinessMetrics {
    return &BusinessMetrics{
        ordersProcessed: promauto.NewCounter(prometheus.CounterOpts{
            Name: "orders_processed_total",
            Help: "Total number of orders processed",
        }),
        revenue: promauto.NewCounter(prometheus.CounterOpts{
            Name: "revenue_total",
            Help: "Total revenue in dollars",
        }),
        averageOrderValue: promauto.NewGauge(prometheus.GaugeOpts{
            Name: "average_order_value",
            Help: "Average order value in dollars",
        }),
        processingTime: promauto.NewHistogram(prometheus.HistogramOpts{
            Name:    "order_processing_seconds",
            Help:    "Time to process an order",
            Buckets: []float64{0.1, 0.5, 1.0, 2.0, 5.0},
        }),
    }
}

func (m *BusinessMetrics) RecordOrder(amount float64, duration time.Duration) {
    m.ordersProcessed.Inc()
    m.revenue.Add(amount)
    m.processingTime.Observe(duration.Seconds())

    // Обновление среднего значения
    total := m.revenue.Get()
    count := m.ordersProcessed.Get()
    if count > 0 {
        m.averageOrderValue.Set(total / count)
    }
}
```

### Практические примеры: OpenTelemetry трейсинг

```go
import (
    "go.opentelemetry.io/otel"
    "go.opentelemetry.io/otel/attribute"
    "go.opentelemetry.io/otel/exporters/jaeger"
    "go.opentelemetry.io/otel/propagation"
    "go.opentelemetry.io/otel/sdk/resource"
    "go.opentelemetry.io/otel/sdk/trace"
    semconv "go.opentelemetry.io/otel/semconv/v1.12.0"
)

func initTracer(serviceName string) (*trace.TracerProvider, error) {
    exporter, err := jaeger.New(jaeger.WithCollectorEndpoint(
        jaeger.WithEndpoint("http://localhost:14268/api/traces"),
    ))
    if err != nil {
        return nil, err
    }

    tp := trace.NewTracerProvider(
        trace.WithBatcher(exporter),
        trace.WithResource(resource.NewWithAttributes(
            semconv.SchemaURL,
            semconv.ServiceNameKey.String(serviceName),
            attribute.String("environment", "production"),
        )),
    )

    otel.SetTracerProvider(tp)
    otel.SetTextMapPropagator(propagation.NewCompositeTextMapPropagator(
        propagation.TraceContext{},
        propagation.Baggage{},
    ))

    return tp, nil
}

func tracedHandler(w http.ResponseWriter, r *http.Request) {
    ctx := r.Context()
    tracer := otel.Tracer("myapp")

    ctx, span := tracer.Start(ctx, "handleRequest",
        trace.WithAttributes(
            attribute.String("http.method", r.Method),
            attribute.String("http.path", r.URL.Path),
        ),
    )
    defer span.End()

    // Вложенный span
    ctx, childSpan := tracer.Start(ctx, "processData")
    processData(ctx)
    childSpan.End()

    span.SetStatus(codes.Ok, "Request processed successfully")
}
```

### Практические примеры: Распределенный трейсинг

```go
func propagateTrace(ctx context.Context, req *http.Request) {
    propagator := otel.GetTextMapPropagator()
    propagator.Inject(ctx, propagation.HeaderCarrier(req.Header))
}

func extractTrace(ctx context.Context, req *http.Request) context.Context {
    propagator := otel.GetTextMapPropagator()
    return propagator.Extract(ctx, propagation.HeaderCarrier(req.Header))
}

func clientRequest(ctx context.Context, url string) error {
    tracer := otel.Tracer("myapp")
    ctx, span := tracer.Start(ctx, "clientRequest")
    defer span.End()

    req, _ := http.NewRequestWithContext(ctx, "GET", url, nil)
    propagateTrace(ctx, req)

    client := &http.Client{}
    resp, err := client.Do(req)
    if err != nil {
        span.RecordError(err)
        return err
    }
    defer resp.Body.Close()

    span.SetAttributes(
        attribute.Int("http.status_code", resp.StatusCode),
    )

    return nil
}
```

### Практические примеры: Структурированное логирование с контекстом

```go
import (
    "log/slog"
    "os"
)

type ContextLogger struct {
    logger *slog.Logger
}

func NewContextLogger() *ContextLogger {
    return &ContextLogger{
        logger: slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
            Level: slog.LevelInfo,
            AddSource: true,
        })),
    }
}

func (l *ContextLogger) WithContext(ctx context.Context) *slog.Logger {
    logger := l.logger

    // Извлечение значений из контекста
    if requestID := ctx.Value("request_id"); requestID != nil {
        logger = logger.With("request_id", requestID)
    }

    if userID := ctx.Value("user_id"); userID != nil {
        logger = logger.With("user_id", userID)
    }

    // Извлечение trace ID из OpenTelemetry
    span := trace.SpanFromContext(ctx)
    if span.SpanContext().IsValid() {
        logger = logger.With(
            "trace_id", span.SpanContext().TraceID().String(),
            "span_id", span.SpanContext().SpanID().String(),
        )
    }

    return logger
}

func (l *ContextLogger) LogRequest(ctx context.Context, method, path string, status int, duration time.Duration) {
    logger := l.WithContext(ctx)
    logger.Info("Request processed",
        "method", method,
        "path", path,
        "status", status,
        "duration_ms", duration.Milliseconds(),
    )
}
```

### Практические примеры: Health checks с деталями

```go
type HealthChecker interface {
    Check(ctx context.Context) error
    Name() string
}

type DatabaseHealthChecker struct {
    db *sql.DB
}

func (c *DatabaseHealthChecker) Name() string {
    return "database"
}

func (c *DatabaseHealthChecker) Check(ctx context.Context) error {
    ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()

    return c.db.PingContext(ctx)
}

type CacheHealthChecker struct {
    cache *redis.Client
}

func (c *CacheHealthChecker) Name() string {
    return "cache"
}

func (c *CacheHealthChecker) Check(ctx context.Context) error {
    ctx, cancel := context.WithTimeout(ctx, 2*time.Second)
    defer cancel()

    return c.cache.Ping(ctx).Err()
}

type HealthService struct {
    checkers []HealthChecker
}

func NewHealthService(checkers ...HealthChecker) *HealthService {
    return &HealthService{checkers: checkers}
}

func (s *HealthService) HealthCheck(w http.ResponseWriter, r *http.Request) {
    ctx := r.Context()
    results := make(map[string]string)
    allHealthy := true

    for _, checker := range s.checkers {
        if err := checker.Check(ctx); err != nil {
            results[checker.Name()] = fmt.Sprintf("error: %v", err)
            allHealthy = false
        } else {
            results[checker.Name()] = "ok"
        }
    }

    statusCode := http.StatusOK
    if !allHealthy {
        statusCode = http.StatusServiceUnavailable
    }

    w.Header().Set("Content-Type", "application/json")
    w.WriteHeader(statusCode)
    json.NewEncoder(w).Encode(map[string]interface{}{
        "status":  map[string]bool{"healthy": allHealthy},
        "checks":  results,
        "timestamp": time.Now().UTC().Format(time.RFC3339),
    })
}
```

### Практические примеры: Readiness и Liveness probes

```go
type ReadinessProbe struct {
    ready bool
    mu    sync.RWMutex
}

func NewReadinessProbe() *ReadinessProbe {
    return &ReadinessProbe{ready: false}
}

func (p *ReadinessProbe) SetReady(ready bool) {
    p.mu.Lock()
    defer p.mu.Unlock()
    p.ready = ready
}

func (p *ReadinessProbe) IsReady() bool {
    p.mu.RLock()
    defer p.mu.RUnlock()
    return p.ready
}

func (p *ReadinessProbe) Handler(w http.ResponseWriter, r *http.Request) {
    if p.IsReady() {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("ready"))
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
        w.Write([]byte("not ready"))
    }
}

type LivenessProbe struct {
    lastHeartbeat time.Time
    mu            sync.RWMutex
    timeout       time.Duration
}

func NewLivenessProbe(timeout time.Duration) *LivenessProbe {
    return &LivenessProbe{
        lastHeartbeat: time.Now(),
        timeout:       timeout,
    }
}

func (p *LivenessProbe) Heartbeat() {
    p.mu.Lock()
    defer p.mu.Unlock()
    p.lastHeartbeat = time.Now()
}

func (p *LivenessProbe) IsAlive() bool {
    p.mu.RLock()
    defer p.mu.RUnlock()
    return time.Since(p.lastHeartbeat) < p.timeout
}

func (p *LivenessProbe) Handler(w http.ResponseWriter, r *http.Request) {
    if p.IsAlive() {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("alive"))
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
        w.Write([]byte("dead"))
    }
}
```

### Практические примеры: Метрики для бизнес-логики

```go
type OrderMetrics struct {
    ordersCreated    prometheus.Counter
    ordersCompleted  prometheus.Counter
    ordersFailed     prometheus.Counter
    orderValue       prometheus.Histogram
    processingTime   prometheus.Histogram
}

func NewOrderMetrics() *OrderMetrics {
    return &OrderMetrics{
        ordersCreated: promauto.NewCounter(prometheus.CounterOpts{
            Name: "orders_created_total",
            Help: "Total number of orders created",
        }),
        ordersCompleted: promauto.NewCounter(prometheus.CounterOpts{
            Name: "orders_completed_total",
            Help: "Total number of orders completed",
        }),
        ordersFailed: promauto.NewCounterVec(
            prometheus.CounterOpts{
                Name: "orders_failed_total",
                Help: "Total number of failed orders",
            },
            []string{"reason"},
        ),
        orderValue: promauto.NewHistogram(prometheus.HistogramOpts{
            Name:    "order_value_dollars",
            Help:    "Order value in dollars",
            Buckets: []float64{10, 50, 100, 500, 1000, 5000},
        }),
        processingTime: promauto.NewHistogram(prometheus.HistogramOpts{
            Name:    "order_processing_seconds",
            Help:    "Time to process an order",
            Buckets: prometheus.DefBuckets,
        }),
    }
}

func (m *OrderMetrics) RecordOrderCreated(value float64) {
    m.ordersCreated.Inc()
    m.orderValue.Observe(value)
}

func (m *OrderMetrics) RecordOrderCompleted(duration time.Duration) {
    m.ordersCompleted.Inc()
    m.processingTime.Observe(duration.Seconds())
}

func (m *OrderMetrics) RecordOrderFailed(reason string) {
    m.ordersFailed.WithLabelValues(reason).Inc()
}
```

### Практические примеры: Интеграция с Grafana

```go
// Экспорт метрик для Prometheus
func setupMetrics() {
    http.Handle("/metrics", promhttp.Handler())
    go http.ListenAndServe(":9090", nil)
}

// Настройка AlertManager для алертов
// alerts.yml
// groups:
//   - name: myapp
//     rules:
//       - alert: HighErrorRate
//         expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
//         for: 5m
//         annotations:
//           summary: "High error rate detected"
```

### Практические примеры: Health checks

```go
type HealthChecker interface {
    Check(ctx context.Context) error
}

type HealthStatus struct {
    Status    string            `json:"status"`
    Checks    map[string]string `json:"checks,omitempty"`
    Timestamp time.Time         `json:"timestamp"`
}

type HealthRegistry struct {
    checkers map[string]HealthChecker
    mu       sync.RWMutex
}

func NewHealthRegistry() *HealthRegistry {
    return &HealthRegistry{
        checkers: make(map[string]HealthChecker),
    }
}

func (hr *HealthRegistry) Register(name string, checker HealthChecker) {
    hr.mu.Lock()
    defer hr.mu.Unlock()
    hr.checkers[name] = checker
}

func (hr *HealthRegistry) Check(ctx context.Context) HealthStatus {
    hr.mu.RLock()
    checkers := make(map[string]HealthChecker)
    for k, v := range hr.checkers {
        checkers[k] = v
    }
    hr.mu.RUnlock()

    status := HealthStatus{
        Status:    "healthy",
        Checks:    make(map[string]string),
        Timestamp: time.Now(),
    }

    for name, checker := range checkers {
        if err := checker.Check(ctx); err != nil {
            status.Status = "unhealthy"
            status.Checks[name] = err.Error()
        } else {
            status.Checks[name] = "ok"
        }
    }

    return status
}

func HealthCheckHandler(registry *HealthRegistry) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
        defer cancel()

        status := registry.Check(ctx)

        w.Header().Set("Content-Type", "application/json")
        if status.Status == "unhealthy" {
            w.WriteHeader(http.StatusServiceUnavailable)
        }

        json.NewEncoder(w).Encode(status)
    }
}
```

### Практические примеры: Distributed tracing с OpenTelemetry

```go
import (
    "go.opentelemetry.io/otel"
    "go.opentelemetry.io/otel/trace"
)

func TraceMiddleware(tr trace.Tracer) func(http.Handler) http.Handler {
    return func(next http.Handler) http.Handler {
        return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            ctx, span := tr.Start(r.Context(), r.URL.Path)
            defer span.End()

            span.SetAttributes(
                attribute.String("http.method", r.Method),
                attribute.String("http.url", r.URL.String()),
            )

            r = r.WithContext(ctx)
            next.ServeHTTP(w, r)
        })
    }
}

func TracedFunction(ctx context.Context, name string, fn func(context.Context) error) error {
    tr := otel.Tracer("app")
    ctx, span := tr.Start(ctx, name)
    defer span.End()

    err := fn(ctx)
    if err != nil {
        span.RecordError(err)
    }

    return err
}
```

### Практические примеры: Собственные метрики

```go
type CustomMetrics struct {
    requestDuration prometheus.HistogramVec
    requestCount    prometheus.CounterVec
    activeRequests  prometheus.Gauge
}

func NewCustomMetrics() *CustomMetrics {
    return &CustomMetrics{
        requestDuration: *prometheus.NewHistogramVec(
            prometheus.HistogramOpts{
                Name: "http_request_duration_seconds",
                Help: "HTTP request duration in seconds",
            },
            []string{"method", "endpoint", "status"},
        ),
        requestCount: *prometheus.NewCounterVec(
            prometheus.CounterOpts{
                Name: "http_requests_total",
                Help: "Total number of HTTP requests",
            },
            []string{"method", "endpoint", "status"},
        ),
        activeRequests: prometheus.NewGauge(
            prometheus.GaugeOpts{
                Name: "http_active_requests",
                Help: "Number of active HTTP requests",
            },
        ),
    }
}

func (cm *CustomMetrics) RecordRequest(method, endpoint, status string, duration time.Duration) {
    cm.requestDuration.WithLabelValues(method, endpoint, status).Observe(duration.Seconds())
    cm.requestCount.WithLabelValues(method, endpoint, status).Inc()
}

func (cm *CustomMetrics) IncrementActive() {
    cm.activeRequests.Inc()
}

func (cm *CustomMetrics) DecrementActive() {
    cm.activeRequests.Dec()
}
```

## Лучшие практики

1. **Используйте структурированное логирование** — для лучшего анализа
2. **Добавляйте контекст** — включайте релевантную информацию
3. **Инструментируйте критические пути** — добавляйте метрики и трейсы
4. **Используйте sampling** — для снижения нагрузки от трейсинга
5. **Мониторьте здоровье системы** — используйте **health checks**
6. **Используйте правильные типы метрик** — **Counter**, **Gauge**, **Histogram**, **Summary**
7. **Добавляйте labels** — для детализации метрик
8. **Экспортируйте метрики** — используйте /**metrics endpoint**
9. **Используйте distributed tracing** — для отслеживания запросов
10. **Настройте алерты** — для критических метрик
11. **Используйте health checks** — для проверки состояния системы
12. **Используйте distributed tracing** — для отслеживания запросов через сервисы
13. **Создавайте собственные метрики** — для специфичных бизнес-метрик
14. **Используйте правильные типы** — выбирайте правильные типы метрик
15. **Мониторьте производительность** — отслеживайте производительность операций


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Наблюдаемость в Go предоставляет мощные инструменты для понимания, мониторинга и отладки приложений. Понимание метрик, трейсинга, логирования, мониторинга, **health checks**, **distributed tracing**, **custom metrics** и практических применений критично для создания наблюдаемых приложений в Go. Правильная настройка наблюдаемости позволяет быстро выявлять и решать проблемы в **production** окружении, обеспечивать высокую доступность и производительность приложений.

## Дополнительные ресурсы

- [Prometheus Go Client](https://github.com/prometheus/client_golang)
- [OpenTelemetry Go](https://opentelemetry.io/docs/go/)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
