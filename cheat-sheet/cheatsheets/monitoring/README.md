# Мониторинг и Observability (Monitoring)

**Комплексные руководства по мониторингу, логированию, трейсингу и визуализации**

## 📋 Описание

Этот раздел содержит детальные руководства по observability стеку: метрики (Prometheus, Micrometer), логирование (ELK Stack, Logback), трейсинг (Jaeger, Zipkin, OpenTelemetry), визуализация (Grafana, Kibana), алертинг (Alertmanager, PagerDuty).

## 📚 Структура раздела

### 📊 [Metrics Collection](metrics-collection/)
**Сбор метрик**

#### [Prometheus](metrics-collection/prometheus/)
- **[Prometheus](metrics-collection/prometheus/prometheus.md)** - Prometheus метрики

#### [Micrometer](metrics-collection/micrometer/)
- **Micrometer** - Application metrics

#### [StatsD](metrics-collection/statsd/)
- **StatsD** - StatsD протокол

### 📈 [Visualization](visualization/)
**Визуализация данных**

#### [Grafana](visualization/grafana/)
- **[Grafana](visualization/grafana/grafana.md)** - Grafana дашборды

#### [Kibana](visualization/kibana/)
- **Kibana** - Kibana визуализация

### 📝 [Logging](logging/)
**Логирование**

- **[Logging Basics](logging/logging-basics.md)** - Основы логирования
- **[Logback](logging/logback.md)** - Logback
- **[Log4j](logging/log4j.md)** - Log4j
- **[SLF4J](logging/slf4j.md)** - SLF4J
- **[Structured Logging](logging/structured-logging.md)** - Структурированное логирование
- **[Log Aggregation](logging/log-aggregation.md)** - Агрегация логов

#### [ELK Stack](logging/elk-stack/)
- **ELK Stack** - Elasticsearch, Logstash, Kibana

#### [Fluentd](logging/fluentd/)
- **Fluentd** - Fluentd сбор логов

### 🔍 [Tracing](tracing/)
**Distributed tracing**

#### [Jaeger](tracing/jaeger/)
- **[Jaeger](tracing/jaeger/jaeger.md)** - Jaeger tracing

#### [Zipkin](tracing/zipkin/)
- **Zipkin** - Zipkin tracing

#### [OpenTelemetry](tracing/opentelemetry/)
- **OpenTelemetry** - OpenTelemetry стандарт

### 🚨 [Alerting](alerting/)
**Алертинг**

- **[Alerting](alerting/alerting.md)** - Основы алертинга

#### [Alertmanager](alerting/alertmanager/)
- **Alertmanager** - Prometheus Alertmanager

#### [PagerDuty](alerting/pagerduty/)
- **PagerDuty** - PagerDuty алертинг

#### [Slack Alerting](alerting/slack-alerting/)
- **Slack Alerting** - Алерты в Slack

### 📡 [APM](apm/)
**Application Performance Monitoring**

- **APM** - APM инструменты

### 🏗️ [Infrastructure Monitoring](infrastructure-monitoring/)
**Мониторинг инфраструктуры**

- **Infrastructure Monitoring** - Мониторинг инфраструктуры

## 🎯 Для кого этот раздел

### DevOps Engineers
- **Настройка мониторинга** - Prometheus, Grafana
- **Логирование** - ELK Stack, Fluentd
- **Алертинг** - Alertmanager, PagerDuty

### Developers
- **Интеграция метрик** - Micrometer, Prometheus
- **Логирование в приложениях** - Logback, SLF4J
- **Distributed tracing** - OpenTelemetry, Jaeger

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Logging
   ├── Logging Basics
   ├── Logback
   └── Structured Logging

2. Metrics
   ├── Micrometer
   └── Prometheus Basics

3. Visualization
   └── Grafana Basics
```

### Для опытных
```
1. Observability Stack
   ├── Metrics (Prometheus)
   ├── Logs (ELK Stack)
   └── Traces (Jaeger/Zipkin)

2. Advanced Topics
   ├── OpenTelemetry
   ├── Alerting
   └── APM
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Libraries](../libraries/)** - Micrometer, OpenTelemetry
- **[Frameworks](../frameworks/)** - Spring Actuator
- **[DevOps](../devops/)** - Infrastructure monitoring
- **[Interview](../interview/monitoring/)** - Вопросы на собеседовании

### Специфичные связи
- **Micrometer** → [Libraries](../libraries/java-micrometer.md)
- **OpenTelemetry** → [Libraries](../libraries/java-opentelemetry.md)
- **Spring Actuator** → [Spring](../frameworks/java-frameworks/spring/spring-actuator.md)
- **Prometheus** → [Metrics Collection](metrics-collection/prometheus/)

## 📚 Полезные ресурсы

### Официальная документация
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [ELK Stack Documentation](https://www.elastic.co/guide/)

### Учебные материалы
- [Prometheus Best Practices](https://prometheus.io/docs/practices/)
- [Observability Engineering](https://www.oreilly.com/library/view/observability-engineering/9781492076438/)

## 🎯 Следующие шаги

После изучения мониторинга:
1. **Изучите библиотеки** → [Libraries](../libraries/)
2. **Освойте DevOps** → [DevOps](../devops/)
3. **Подготовьтесь к интервью** → [Interview](../interview/monitoring/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../security/)

*Обновлено: 2026-01-25*
