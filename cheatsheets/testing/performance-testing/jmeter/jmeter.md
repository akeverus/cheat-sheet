---
title: "Apache JMeter"
description: "Кратко: Apache JMeter — инструмент нагрузочного и функционального тестирования. Thread Group, Samplers (HTTP Request, JDBC и др.), Listeners, Assertions; GUI для построения планов, CLI для запуска; отчёты, CI/CD."
tags:
  - testing
  - performance-testing
  - jmeter
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Apache JMeter

**Кратко:** Apache JMeter — инструмент нагрузочного и функционального тестирования. Thread Group, Samplers (HTTP Request, JDBC и др.), Listeners, Assertions; GUI для построения планов, CLI для запуска; отчёты, CI/CD.


## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Официально | [Apache JMeter](https://jmeter.apache.org/), [User's Manual](https://jmeter.apache.org/usermanual/index.html) |
| Практики | [Building a Test Plan](https://jmeter.apache.org/usermanual/build-test-plan.html), [Best Practices](https://jmeter.apache.org/usermanual/best-practices.html) |
| Обзор | [gatling](../gatling/gatling.md), [k6](../k6/k6.md), [artillery](../artillery/artillery.md), [Testing Tools Overview](../../testing-tools/testing-tools-overview.md) |


## Содержание

- [Введение](#введение)
  - [Зачем JMeter](#зачем-jmeter)
  - [Основные концепции](#основные-концепции)
- [Установка и настройка](#установка-и-настройка)
- [Элементы Test Plan](#элементы-test-plan)
  - [Thread Group](#thread-group)
  - [Samplers](#samplers)
  - [Listeners](#listeners)
  - [Assertions](#assertions)
- [Контроллеры и таймеры (кратко)](#контроллеры-и-таймеры-кратко)
- [Pre- и Post-процессоры](#pre-и-post-процессоры)
- [Базовое использование](#базовое-использование)
  - [Минимальный план (GUI)](#минимальный-план-gui)
  - [CLI](#cli)
- [Запуск: GUI и CLI](#запуск-gui-и-cli)
- [Отчёты и метрики](#отчёты-и-метрики)
- [Параметризация и переменные](#параметризация-и-переменные)
- [Пример: логин и запрос с токеном](#пример-логин-и-запрос-с-токеном)
- [CI/CD](#cicd)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Справка: команды CLI и метрики](#справка-команды-cli-и-метрики)
  - [Основные опции](#основные-опции)
  - [Метрики (Summary Report / Aggregate Report)](#метрики-summary-report-aggregate-report)
- [Сравнение с Gatling и k6](#сравнение-с-gatling-и-k6)
- [Заключение](#заключение)

## Введение

**Apache JMeter** — открытый инструмент для нагрузочного, стрессового и функционального тестирования. Изначально для веб (HTTP/HTTPS), также поддерживает **FTP**, **JDBC**, **JMS**, **SOAP**, **LDAP**, **TCP**. Тест описывается как **Test Plan** (дерево элементов); выполнение — в GUI (отладка) или в **non-GUI** (CLI) для нагрузочных прогонов.

### Зачем JMeter

- **Нагрузочное тестирование** — симуляция многих пользователей (потоков), пропускная способность и время отклика.
- **Стресс-тестирование** — рост нагрузки до предела, поиск узких мест.
- **Функциональное тестирование** — проверка ответов (Assertions).
- **GUI + CLI** — план в GUI, запуск в CLI без накладных расходов на отрисовку.
- **Расширяемость** — плагины, кастомные Samplers/Listeners, скрипты (Groovy, BeanShell, JSR223).

### Основные концепции

| Элемент | Назначение |
|---------|------------|
| **Test Plan** | Корень; Thread Groups и общие настройки (переменные, библиотеки) |
| **Thread Group** | Группа виртуальных пользователей: число потоков, ramp-up, число итераций (loop count) |
| **Sampler** | Отправляет запрос (HTTP Request, JDBC Request, TCP Request и т.д.) |
| **Listener** | Собирает и показывает результаты (View Results Tree, Summary Report и т.д.) |
| **Assertion** | Проверка ответа (код, тело, время) |
| **Pre/Post Processors** | Логика до/после Sampler (извлечение данных, подстановка переменных) |
| **Timer** | Задержка между запросами (Constant Timer, Random Timer) |
| **Controller** | Группировка (Simple, Loop, If, Transaction, Throughput и т.д.) |


## Установка и настройка

**Требования:** Java 8+ (рекомендуется 11 или 17). Проверка: `java -version`.

**Шаги:**

1. Скачать бинарный архив с [jmeter.apache.org](https://jmeter.apache.org/download_jmeter.cgi).
2. Распаковать (например, `/opt/jmeter` или `C:\jmeter`).
3. GUI: `bin/jmeter` (Unix/macOS) или `bin\jmeter.bat` (Windows).
4. CLI: `bin/jmeter -n -t test.jmx -l result.jtl`.

Увеличение heap для больших нагрузок:

```bash
export JVM_ARGS="-Xms1g -Xmx4g"
bin/jmeter -n -t plan.jmx -l results.jtl
```

Проверка: `bin/jmeter --version`.


## Элементы Test Plan

### Thread Group

- **Number of Threads (users)** — количество виртуальных пользователей.
- **Ramp-up period** — время выхода на полное число потоков (например, 60 с при 100 потоках).
- **Loop Count** — сколько раз каждый поток выполняет дочерние элементы (или «Forever» с ограничением по времени).
- **Scheduler** — опционально: начало, конец, длительность.

Дочерние элементы выполняются сверху вниз; при Loop Count > 1 цикл повторяется.

### Samplers

**HTTP Request** — самый частый:

- Protocol (http/https), Server Name, Port, Path, Method.
- Parameters / Body Data — параметры или тело (JSON, XML).

**JDBC Request** — запрос к БД. Нужен **JDBC Connection Configuration** (URL, драйвер, пользователь, пароль). В Query — SQL, можно переменные `${var}`.

**Другие:** FTP Request, TCP Request, SMTP Request, JMS, SOAP/XML-RPC и т.д.

### Listeners

| Listener | Назначение |
|----------|------------|
| **View Results Tree** | Детальный просмотр запроса/ответа (только для отладки; в прогоне отключать — грузит память) |
| **Summary Report** | Сводка: Label, # Samples, Average, Min, Max, Error %, Throughput |
| **Aggregate Report** | Сводка с дополнительными метриками |
| **Backend Listener** | Отправка метрик в InfluxDB, Graphite (дашборды) |
| **Simple Data Writer** | Запись в CSV/JTL для анализа |

В non-GUI обычно: `-l result.jtl`, затем `-g result.jtl -o report/` для HTML-отчёта.

### Assertions

- **Response Assertion** — код ответа, текст в ответе, время отклика.
- **JSON Assertion** — проверка полей по JSONPath.
- **Duration Assertion** — ответ быстрее заданного времени (мс).
- **Size Assertion** — размер ответа в байтах.

Assertion вешается на Sampler (или контроллер — тогда на все Samplers внутри). При падении запрос — Failed.


## Контроллеры и таймеры (кратко)

- **Loop Controller** — повтор дочерних N раз.
- **If Controller** — выполнение по условию (например, `${token}` не пусто).
- **Transaction Controller** — группа Samplers как одна «транзакция» в отчёте.
- **Throughput Controller** — доля итераций (например, 30% — поиск, 70% — главная).
- **Constant Timer** / **Uniform Random Timer** — задержка между запросами (think time).


## Pre- и Post-процессоры

- **JSON Extractor** — значение по JSONPath в переменную (например, `$.access_token` `token`).
- **Regular Expression Extractor** — извлечение по regex из тела/заголовков.
- **HTTP Header Manager** — заголовки запроса (глобально или для Sampler).
- **JSR223 PreProcessor / PostProcessor** — скрипт Groovy до/после Sampler (расчёт переменных, разбор ответа). Предпочтительнее BeanShell.


## Базовое использование

### Минимальный план (GUI)

1. Test Plan Thread Group (потоки: 10, ramp-up: 5, loop: 2).
2. Под Thread Group HTTP Request (Server: `example.com`, Path: `/`).
3. Listener: View Results Tree (отладка) или Summary Report (сводка).
4. Запуск: Run (Ctrl+R).

### CLI

```bash
bin/jmeter -n -t plan.jmx -l results.jtl -j log.txt
```

- **-n** — non-GUI.
- **-t plan.jmx** — файл плана.
- **-l results.jtl** — файл результатов.
- **-j log.txt** — лог JMeter.

HTML-отчёт после прогона:

```bash
bin/jmeter -n -t plan.jmx -l results.jtl -e -o report/
```

**-e** — сгенерировать отчёт, **-o report/** — каталог для HTML.


## Запуск: GUI и CLI

- **GUI** — создание и отладка; не использовать для больших нагрузок (память и CPU на отрисовку).
- **CLI (non-GUI)** — для реальных нагрузочных тестов; минимальные накладные расходы. Рекомендуется увеличить heap (JVM_ARGS) при многих потоках и длительных прогонах.

Типичный порядок: создать и проверить план в GUI (мало потоков, 1–2 итерации), сохранить .jmx, затем запускать в CLI. Число потоков можно переопределять через **-J** или свойства.


## Отчёты и метрики

- **Throughput** — запросов в секунду (успешных).
- **Response time** — Average, Min, Max, перцентили.
- **Error %** — доля неуспешных запросов.
- **Active threads** — число одновременно работающих потоков.

После прогона с **-e -o report/** в **report/** создаётся HTML-дашборд (графики по времени отклика, throughput, ошибки). Для Grafana и др. — **Backend Listener** (InfluxDB, Graphite).


## Параметризация и переменные

- **User Defined Variables** (Test Plan / Thread Group): пары имя=значение; в элементах — `${VAR_NAME}`.
- **CSV Data Set Config** — чтение из CSV; переменные по колонкам; Sharing mode (All threads / Current thread и т.д.).
- **Встроенные:** `__threadNum`, `__threadGroupName`, `__Random(min,max)`, `__time()`, `__FileToString(path,,)`, `__UUID()`, `__counter(FALSE,)`.
- **Свойства:** `${__P(propName, default)}` — из **-J** или user.properties. Пример: в Thread Group — `${__P(threads,10)}`, запуск: `jmeter -n -t plan.jmx -Jthreads=100 -l out.jtl`.

Полный список функций: **Options Function Helper Dialog** в GUI.


## Пример: логин и запрос с токеном

1. **Thread Group** — 10 потоков, ramp-up 5, 1 loop.
2. **CSV Data Set Config** — файл с колонками `user,pass`, Variable Names: `user,pass`.
3. **HTTP Request** — POST `/api/login`, Body Data: `{"username":"${user}","password":"${pass}"}`.
4. **JSON Extractor** — переменная `token`, JSONPath `$.access_token`.
5. **HTTP Header Manager** (для следующего запроса) — `Authorization: Bearer ${token}`.
6. **HTTP Request** — GET `/api/me`.
7. **Response Assertion** — Response Code equals `200`.
8. **Summary Report** — сводка.


## CI/CD

**Jenkins:** шаг «Execute shell» — запуск `jmeter -n -t plan.jmx -l results.jtl -e -o report/`. Публикация артефактов: results.jtl, report/. Плагины «JMeter» или «Performance Plugin» — графики по JTL.

**GitHub Actions:** скачать JMeter, распаковать, запустить так же; артефакты — report/, results.jtl.

Переопределение параметров: в плане Number of Threads = `${__P(threads,10)}`, Ramp-up = `${__P(rampup,5)}`; запуск: `jmeter -n -t plan.jmx -Jthreads=100 -Jrampup=60 -l results.jtl`.


## Лучшие практики

1. **Не гонять нагрузку в GUI** — только отладка; прогоны в CLI.
2. **Отключать тяжёлые Listeners** в прогоне (особенно View Results Tree); использовать Simple Data Writer / Backend Listener или только -l.
3. **Достаточный heap** — JVM_ARGS=-Xms1g -Xmx4g (или больше) в зависимости от потоков и длительности.
4. **Ramp-up** — плавный выход на нагрузку (60–120 с для 100 потоков).
5. **Фиксировать версии** JMeter и Java в CI.
6. **Переменные** для хоста, порта, путей — переключение окружений без правки плана.
7. **Assertions** — минимально нужный набор.
8. **Секреты** — не в .jmx; user.properties (не в репо) или -J из окружения.


## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| OutOfMemoryError | Мало heap, тяжёлые Listeners | Увеличить -Xmx; отключить View Results Tree в прогоне |
| Высокий Error % | Таймауты, перегрузка, неверные Assertions | Увеличить таймауты; проверить Assertions; снизить нагрузку |
| Низкий throughput | Таймауты, узкое место сервера/сети | Сеть, ресурсы сервера; при необходимости уменьшить think time |
| JMeter не стартует | Нет Java или не та версия | Java 8+; проверить java -version |
| Пустой/неполный JTL | Не выбран формат в Listener | В Simple Data Writer выбрать поля; убедиться в -l |
| Разные результаты в CI | Разные версии JMeter/Java, пути | Фиксировать версии; относительные пути, -J для параметров |

**JMeter или Gatling/k6?** JMeter — GUI, много протоколов из коробки, низкий порог входа. Gatling/k6 — сценарии в коде, меньше ресурсов на поток, удобные отчёты. Выбор по команде и требованиям.

**API с авторизацией?** HTTP Header Manager — заголовок `Authorization: Bearer ${token}`. Токен — первый запрос (логин), JSON Extractor или JSR223 PostProcessor переменная.

**Тело запроса из файла?** В Body Data: `${__FileToString(path/to/file.json,,)}` или чтение в JSR223 PreProcessor.

**Распределённый запуск?** Режим master/slave: на машинах-агентах — jmeter-server; в user.properties на master — remote_hosts=agent1:1099,agent2:1099; запуск «Remote Start All». Нужны совместимые версии JMeter/Java и открытые порты RMI.


## Справка: команды CLI и метрики

### Основные опции

| Опция | Описание |
|-------|----------|
| -n | Non-GUI |
| -t file.jmx | Файл Test Plan |
| -l file.jtl | Файл результатов |
| -j log.txt | Лог JMeter |
| -e | Сгенерировать HTML-отчёт после теста |
| -o dir | Каталог для HTML-отчёта |
| -Jprop=val | Свойство (__P(prop)) |

### Метрики (Summary Report / Aggregate Report)

| Метрика | Описание |
|---------|----------|
| Label | Имя Sampler (или Transaction) |
| # Samples | Количество запросов |
| Average / Min / Max | Время отклика (мс) |
| Error % | Доля неуспешных |
| Throughput | Запросов в секунду |


## Сравнение с Gatling и k6

| Критерий | JMeter | Gatling | k6 |
|----------|--------|---------|-----|
| Интерфейс | GUI + CLI | Код (Scala) + CLI | Код (JS) + CLI |
| Протоколы | HTTP, JDBC, FTP, JMS, TCP, ... | HTTP, WebSocket, gRPC | HTTP, WebSocket, gRPC |
| Порог входа | Низкий | Средний | Низкий |
| Ресурсы на поток | Выше | Ниже | Ниже |
| Отчёты | Summary, HTML, Backend Listener | HTML, Grafana | Встроенные, Grafana/InfluxDB |


## Заключение

Apache JMeter — инструмент нагрузочного и функционального тестирования с поддержкой многих протоколов. Построение плана в GUI и запуск в CLI дают удобство отладки и минимальные накладные расходы при нагрузке. Используйте переменные и свойства для параметризации и CI; отключайте тяжёлые Listeners в прогонах и выделяйте достаточный heap. Дальше: [JMeter User's Manual](https://jmeter.apache.org/usermanual/index.html), [gatling](../gatling/gatling.md), [k6](../k6/k6.md), [artillery](../artillery/artillery.md), [Testing Tools Overview](../../testing-tools/testing-tools-overview.md).
