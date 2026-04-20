---
title: "Infrastructure Monitoring (обзор)"
description: "Кратко: Infrastructure Monitoring — мониторинг инфраструктуры: серверы (CPU, память, диск), сеть, контейнеры, виртуализация. Инструменты: Prometheus + Node Exporter, cAdvisor, Grafana, Datadog, Zabbix и др. Цели: доступность, использование ресурсов, планирование мощностей, алерти"
tags:
  - monitoring
  - infrastructure-monitoring
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Infrastructure Monitoring (обзор)

Кратко: **Infrastructure Monitoring** — мониторинг инфраструктуры: серверы (CPU, память, диск), сеть, контейнеры, виртуализация. Инструменты: **Prometheus** + **Node Exporter**, **cAdvisor**, **Grafana**, **Datadog**, **Zabbix** и др. Цели: доступность, использование ресурсов, планирование мощностей, алертинг.

## Полезные ссылки

### Официальная документация
- [Prometheus — Node Exporter](https://github.com/prometheus/node_exporter)
- [cAdvisor](https://github.com/google/cadvisor)
- [Grafana](https://grafana.com/docs/)

### Ресурсы
- [Prometheus — Best practices](https://prometheus.io/docs/practices/)
- [Google — SRE Book (Monitoring)](https://sre.google/sre-book/monitoring-distributed-systems/)

### См. также
- [[prometheus|Prometheus]] — сбор метрик
- [[alertmanager|Alertmanager]] — алертинг
- [Monitoring](./) — раздел мониторинга

## Содержание

- [Введение](#введение)
- [Основные метрики инфраструктуры](#основные-метрики-инфраструктуры)
- [Инструменты сбора](#инструменты-сбора)
- [Node Exporter](#node-exporter)
- [cAdvisor и контейнеры](#cadvisor-и-контейнеры)
- [Сетевой мониторинг](#сетевой-мониторинг)
- [Хранение и визуализация](#хранение-и-визуализация)
- [Алертинг](#алертинг)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Итоговые таблицы](#итоговые-таблицы)
- [Заключение](#заключение)


## Введение

**Infrastructure Monitoring** — практика сбора, хранения и анализа метрик инфраструктуры: хостов (CPU, память, диск, сеть), контейнеров (**Docker**, **Kubernetes**), сетевого оборудования и виртуальных машин. Цели: обеспечить доступность сервисов, выявлять узкие места, планировать масштабирование и реагировать на инциденты по алертам.

**Зачем мониторить инфраструктуру:**

- **Доступность** — знать, работают ли хосты и сервисы (up/down, health checks).
- **Использование ресурсов** — CPU, память, диск, сеть; выявление перегрузок и утечек.
- **Планирование** — тренды роста нагрузки, необходимость добавления узлов или диска.
- **Диагностика** — связь между метриками инфраструктуры и метриками приложений (латентность, ошибки).
- **Алертинг** — уведомления при падении узла, нехватке диска, высокой загрузке.


## Основные метрики инфраструктуры

| Категория | Примеры метрик | Назначение |
|-----------|----------------|------------|
| **CPU** | Использование (%), load average (1, 5, 15 мин), steal, iowait | Загрузка процессора, очереди |
| **Память** | Использовано/свободно, swap, cache, buffers | Утечки, нехватка RAM |
| **Диск** | Использованное место (%), I/O (read/write), latency | Место, производительность диска |
| **Сеть** | Трафик (bytes/packets in/out), ошибки, drops | Нагрузка и сбои сети |
| **Контейнеры** | CPU/memory per container, restart count | Ресурсы и стабильность подов |
| **Система** | Uptime, количество процессов, open file descriptors | Общее состояние хоста |

Источники: **Node Exporter** (хост), **cAdvisor** (контейнеры), **snmp_exporter** (сетевое оборудование), агенты **Datadog**/**Zabbix**.


## Инструменты сбора

| Инструмент | Назначение | Типичное использование |
|------------|------------|-------------------------|
| **Node Exporter** | Метрики хоста (Linux/Unix) для **Prometheus** | Каждый сервер — один **Node Exporter** |
| **cAdvisor** | Метрики контейнеров (**Docker**, **containerd**) | На каждой ноде с контейнерами |
| **snmp_exporter** | Метрики по **SNMP** (свитчи, маршрутизаторы) | **Prometheus** scrape **snmp_exporter** по целевым устройствам |
| **blackbox_exporter** | Проверка доступности (HTTP, TCP, ICMP, DNS) | **Prometheus** опрашивает **blackbox_exporter** по списку целей |
| **Datadog Agent** | Универсальный агент (метрики, логи, APM) | Альтернатива/дополнение к **Prometheus** |
| **Zabbix Agent** | Метрики для **Zabbix** | В экосистеме **Zabbix** |
| **Collectd** / **Telegraf** | Сбор метрик с хостов, экспорт в **InfluxDB** и др. | Гибридные стеки |

В стеке **Prometheus** чаще всего используют **Node Exporter** + **cAdvisor** на узлах и **Prometheus Server** для scrape и хранения.


## Node Exporter

**Node Exporter** — экспортер метрик хоста для **Prometheus**. Один бинарник на хост; **Prometheus** выполняет **HTTP** scrape эндпоинта **:9100/metrics**. Метрики в формате **Prometheus** (текст).

**Основные метрики:**

- **node_cpu_seconds_total** — время CPU по режимам (user, system, iowait, idle и т.д.).
- **node_memory_MemTotal_bytes**, **node_memory_MemAvailable_bytes** — память.
- **node_filesystem_size_bytes**, **node_filesystem_avail_bytes** — размер и свободное место по точкам монтирования.
- **node_network_receive_bytes_total**, **node_network_transmit_bytes_total** — сетевой трафик по интерфейсам.
- **node_load1**, **node_load5**, **node_load15** — load average.
- **node_filefd_allocated**, **node_filefd_maximum** — файловые дескрипторы.

**Установка (пример на Linux):**

```bash
wget https://github.com/prometheus/node_exporter/releases/download/v1.7.0/node_exporter-1.7.0.linux-amd64.tar.gz
tar xzf node_exporter-1.7.0.linux-amd64.tar.gz
cd node_exporter-1.7.0.linux-amd64
./node_exporter
```

**Конфигурация Prometheus (scrape):**

```yaml
scrape_configs:
  - job_name: 'node'
    static_configs:
      - targets: ['host1:9100', 'host2:9100']
```

По умолчанию **Node Exporter** собирает много коллекторов; при необходимости отключить лишние флагом `--collector.disable-defaults` и `--collectors.enable=...`.


## cAdvisor и контейнеры

**cAdvisor** (Container Advisor) — собирает метрики контейнеров: CPU, память, сеть, диск по контейнеру. Поддерживает **Docker**, **containerd**, **rkt**. Экспортирует метрики в формате **Prometheus** на порту **8080** (по умолчанию).

**Типичные метрики:**

- **container_cpu_usage_seconds_total** — использование CPU контейнером.
- **container_memory_usage_bytes** — использование памяти.
- **container_network_receive_bytes_total**, **container_network_transmit_bytes_total** — сеть контейнера.
- **container_fs_usage_bytes** — использование файловой системы контейнером.

В **Kubernetes** **cAdvisor** часто встроен в **kubelet**; метрики доступны на эндпоинте **kubelet** (например, **10250**). Либо разворачивают отдельный **cAdvisor** в **DaemonSet**.

**Запуск (Docker):**

```bash
docker run -d --name cadvisor \
  -v /:/rootfs:ro -v /var/run:/var/run:ro \
  -v /sys:/sys:ro -v /var/lib/docker/:/var/lib/docker:ro \
  -p 8080:8080 \
  gcr.io/cadvisor/cadvisor:latest
```

**Prometheus** добавляет scrape для **cAdvisor** (job **cadvisor** или **kubernetes-nodes-cadvisor** в **Kubernetes**).


## Сетевой мониторинг

- **Трафик и ошибки** — **Node Exporter** даёт **node_network_*** по интерфейсам. Алерты на рост ошибок (drops, errors).
- **Доступность** — **blackbox_exporter** проверяет HTTP/TCP/ICMP до целевых хостов и сервисов; **Prometheus** собирает метрики успеха/таймаута и латентности.
- **SNMP** — для свитчей и маршрутизаторов используют **snmp_exporter**: конфиг с OID, **Prometheus** scrape по списку устройств; метрики (трафик, ошибки, состояние портов) попадают в **Prometheus**.


## Хранение и визуализация

**Prometheus** хранит метрики локально (или удалённо при **Thanos**/ **VictoriaMetrics**). Визуализация: **Grafana** (подключение к **Prometheus** как data source), дашборды по **Node Exporter** и **cAdvisor** (готовые дашборды в сообществе **Grafana**). Альтернативы: **Datadog**, **New Relic** — свои агенты и облачное хранение с готовыми дашбордами по инфраструктуре.


## Алертинг

Правила алертинга задаются в **Prometheus** (или в системе мониторинга). Примеры: **InstanceDown** (up == 0), **HighMemoryUsage** (доля использованной памяти > 90%), **DiskSpaceLow** (свободно < 10%), **HighCPU** (load или использование CPU выше порога). Алерты направляются в **Alertmanager** и далее в **Slack**, **PagerDuty** и т.д. См. [Alertmanager](alerting/alertmanager.md).


## Лучшие практики

1. **Один Node Exporter на хост** — не дублировать; **Prometheus** scrape по списку хостов.
2. **Метки (labels)** — добавлять метки окружения (env), роли (role), датацентра при необходимости (relabel в **Prometheus** или в экспортере).
3. **Интервал scrape** — для инфраструктуры обычно 15–30 с; не делать слишком частым из-за нагрузки.
4. **Хранение** — оценивать объём данных **Prometheus**; при длительном хранении рассматривать **Thanos** или **VictoriaMetrics**.
5. **Алерты** — не алертить на всё подряд; ввести уровни (critical, warning), использовать **for** для снижения шума (условие должно держаться N минут).
6. **Контейнеры** — в **Kubernetes** использовать метки **pod**, **namespace**, **container** для группировки и алертов по подам/контейнерам.


## Решение проблем

| Симптом | Возможная причина | Действие |
|---------|-------------------|----------|
| Метрики Node Exporter не появляются в **Prometheus** | Сеть, firewall, неверный **scrape_config** | Проверить доступность **host:9100** с хоста **Prometheus**; проверить **targets** в **Prometheus** UI |
| Высокая нагрузка на хост от **Node Exporter** | Слишком много коллекторов или частый scrape | Отключить ненужные коллекторы; увеличить scrape interval |
| Метрики контейнеров отсутствуют | **cAdvisor** не запущен или не доступен; в **Kubernetes** — эндпоинт **kubelet** | Проверить **cAdvisor**/kubelet на нодах; проверить **serviceAccount** и доступ **Prometheus** к **kubelet** |
| Алерты не срабатывают | Неверное выражение или порог в правиле **Prometheus** | Проверить правило в **Prometheus** (вкладка **Alerts**); проверить наличие метрик и выражение в **Graph** |
| **Prometheus** не хватает места на диске | Рост объёма данных | Увеличить **retention** или уменьшить частоту/количество метрик; рассмотреть удалённое хранение |


## Частые вопросы

**Нужен ли отдельный Prometheus для инфраструктуры?**
Не обязательно; один **Prometheus** может scrape и приложение, и **Node Exporter**/ **cAdvisor**. Разделение по окружениям (prod/staging) или по назначению (infra vs app) делают при больших объёмах или разных политик retention.

**Как мониторить Windows-хосты?**
**Node Exporter** официально для Linux/Unix. Для **Windows** используют **windows_exporter** (аналог для Windows) или агенты **Datadog**/ **Zabbix**.

**Чем cAdvisor отличается от метрик kubelet?**
В **Kubernetes** **cAdvisor** встроен в **kubelet**; метрики контейнеров доступны на эндпоинте **kubelet** (**/metrics/cadvisor**). Отдельный **cAdvisor** в **DaemonSet** используют при необходимости другой конфигурации или версии.

**Как мониторить сеть между сервисами?**
Трафик между подами/сервисами можно оценивать по метрикам **cAdvisor**/kubelet по интерфейсам; для детального анализа сети в **Kubernetes** используют **Service Mesh** (метрики из **Istio**, **Linkerd**) или eBPF-инструменты.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **Node Exporter** | Экспортер метрик хоста для **Prometheus** |
| **cAdvisor** | Сборщик метрик контейнеров (**Docker**, **containerd**) |
| **Scrape** | Опрос **Prometheus** эндпоинта экспортера по HTTP для получения метрик |
| **Load average** | Средняя длина очереди процессов (1, 5, 15 минут) |
| **SNMP** | Протокол управления сетевыми устройствами; метрики через **snmp_exporter** |
| **blackbox_exporter** | Проверка доступности целей (HTTP, TCP, ICMP, DNS) для **Prometheus** |


## Итоговые таблицы

### Метрики Node Exporter (выборка)

| Метрика | Описание |
|---------|----------|
| node_cpu_seconds_total | Время CPU по режимам (user, system, idle, iowait и т.д.) |
| node_memory_MemAvailable_bytes | Доступная память (для расчёта использования) |
| node_filesystem_avail_bytes | Свободное место на ФС |
| node_network_receive_bytes_total | Входящий трафик по интерфейсу |
| node_load1, node_load5, node_load15 | Load average |
| node_up | 1 если scrape успешен (для алерта InstanceDown) |

### См. также (повтор)

- [Prometheus](metrics/prometheus.md) — сбор и хранение метрик
- [Alertmanager](alerting/alertmanager.md) — алертинг
- [Monitoring](./) — раздел мониторинга


## Заключение

**Infrastructure Monitoring** — основа наблюдаемости: метрики хостов и контейнеров через **Node Exporter** и **cAdvisor**, сбор в **Prometheus**, визуализация в **Grafana**, алертинг через **Alertmanager**. Настройте scrape для всех узлов и контейнеров, определите ключевые метрики и пороги алертов, следуйте лучшим практикам по меткам и интервалам. См. [Node Exporter](https://github.com/prometheus/node_exporter), [cAdvisor](https://github.com/google/cadvisor), [Prometheus](metrics/prometheus.md), [Alertmanager](alerting/alertmanager.md).


