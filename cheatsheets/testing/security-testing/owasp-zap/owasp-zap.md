---
title: "OWASP ZAP"
description: "Кратко: OWASP ZAP (Zed Attack Proxy) — открытый инструмент тестирования безопасности веб-приложений: proxy (перехват трафика), Scanner (пассивное и активное сканирование), Fuzzer, REST API для автоматизации, скрипты (Zest, Python), отчёты, интеграция с CI/CD."
tags:
  - testing
  - security-testing
  - owasp-zap
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# OWASP ZAP

**Кратко:** OWASP ZAP (Zed Attack Proxy) — открытый инструмент тестирования безопасности веб-приложений: **proxy** (перехват трафика), **Scanner** (пассивное и активное сканирование), **Fuzzer**, **REST API** для автоматизации, скрипты (Zest, Python), отчёты, интеграция с CI/CD.

**Дата:** 2026-02-06

## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Сайт | [zaproxy.org](https://www.zaproxy.org/) |
| Документация | [Getting Started](https://www.zaproxy.org/docs/getting-started/) |
| API | [ZAP API](https://www.zaproxy.org/docs/api/) |
| Automation Framework | [Automation Framework](https://www.zaproxy.org/docs/automate/automation-framework/) |

**См. также:** [[burp-suite|Burp Suite]], [[sqlmap]], [[testing-tools-overview|Testing Tools Overview]]

## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Proxy](#proxy)
- [Scanner (активный и пассивный)](#scanner-активный-и-пассивный)
- [Fuzzer](#fuzzer)
- [API и автоматизация](#api-и-автоматизация)
- [Скрипты (Zest, Python)](#скрипты-zest-python)
- [Отчёты](#отчёты-и-экспорт)
- [Лучшие практики](#лучшие-практики)
- [FAQ и решение проблем](#faq-и-решение-проблем)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)


## Введение

**OWASP ZAP** — бесплатный инструмент с открытым исходным кодом от OWASP для тестирования веб-приложений.

- **Proxy** — перехват и изменение HTTP/HTTPS трафика
- **Пассивное сканирование** — анализ трафика без дополнительных запросов
- **Активное сканирование** — отправка запросов с payload (SQLi, XSS и т.д.)
- **Spider** — обход приложения (ссылки, формы), построение карты URL
- **REST API** и headless-режим для CI/CD
- **Скрипты** (Zest, Python, JavaScript) для расширения

### Зачем ZAP

- Бесплатно и открыто — подходит для команд и обучения
- Встроенный пассивный и активный сканер
- API и headless — Jenkins, GitHub Actions, скрипты
- Расширяемость — дополнения из Marketplace, свои скрипты


## Установка и настройка

### Требования

- **Java** 11+ (рекомендуется 17)
- ОС: Windows, macOS, Linux

### Установка

1. Скачать с [zaproxy.org/download](https://www.zaproxy.org/download/) (Windows — установщик; Linux — пакет; macOS — DMG или Homebrew).
2. **Linux (Debian/Ubuntu):** `sudo apt install zaproxy`
3. **Docker (быстрый скан без GUI):**
   ```bash
   docker run -t owasp/zap2docker-stable zap.sh -cmd -quickurl https://example.com -quickout report.html
   ```

### Настройка браузера

| Шаг | Действие |
|-----|-----------|
| 1 | ZAP: **Edit → Options → Local Proxies** — порт по умолчанию `127.0.0.1:8080` |
| 2 | Браузер: ручной HTTP/HTTPS прокси → `127.0.0.1`, порт `8080` |
| 3 | Установить CA-сертификат ZAP для HTTPS: открыть `http://zap` в браузере или **Edit → Options → Dynamic SSL Certificates** → установить в доверенные |


## Proxy

- **Breakpoints** — в панели **Break** включить перехват запросов/ответов; редактировать и нажать **Submit** или **Drop**.
- **History** — все запросы и ответы; фильтры по хосту, методу, статусу. Действия: **Resend**, **Fuzz**, **Attack → Active Scan**.
- **Sites** — дерево хостов и путей; ПКМ по узлу → **Include in Context** для задания области сканирования.
- **Context** — набор URL (in scope) и настроек; в **Context → Authentication** задаётся форма входа (Form-based, JSON, HTTP Auth); **Session Management** — как определять активную сессию (cookie, заголовок).


## Scanner (активный и пассивный)

### Пассивное сканирование

- Включено по умолчанию. Находит: утечки заголовков (X-Powered-By, Server), отсутствие заголовков безопасности, cookie без Secure/HttpOnly, версии ПО.
- Результаты в **Alerts** (фильтр по риску: High, Medium, Low, Info).

### Активное сканирование

- **Attack → Active Scan** — для выбранного узла/URL отправляются запросы с payload (SQLi, XSS, Path Traversal и др.).
- **Analyze → Scan Policy** — какие проверки выполнять, уровень агрессивности; на продакшене можно отключить деструктивные проверки.
- В **Alerts**: тип, риск, URL, параметр, доказательство (request/response), рекомендации.

### Spider

- **Attack → Spider** — обход по ссылкам и формам, расширение дерева **Sites**. Может использовать аутентификацию из контекста. В настройках: глубина, лимит узлов, таймауты, исключения.


## Fuzzer

- Выделить запрос в **History** → ПКМ → **Attack → Fuzz**. Выбрать позиции (параметры, заголовки, тело) и загрузить списки payload (файл или встроенные).
- **Fuzz type:** Sniper (одна позиция), Pitchfork/Cluster bomb (несколько списков). **Message Processors** — кодирование (URL, Base64 и т.д.) перед подстановкой.
- Результаты в таблице: статус, длина, время; удобно искать аномалии по разным payload.


## API и автоматизация

### REST API

- По умолчанию `http://127.0.0.1:8080`. Ключ API: **Edit → Options → API**. Справка: **Help → API** или [zaproxy.org/docs/api](https://www.zaproxy.org/docs/api/).

Основные вызовы:

| Область | Действие |
|---------|----------|
| core | newSession, accessUrl |
| spider | action scan url=URL |
| ascan | action scan url=URL |
| alert | view alerts, view alertsByRisk |
| report | core generate (HTML) |

Пример запуска активного сканирования:

```bash
curl "http://127.0.0.1:8080/JSON/ascan/action/scan/?url=https://example.com&apikey=KEY"
```

### Headless и CI/CD

- **Headless:** `zap.sh -cmd -quickurl https://example.com -quickout report.html`
- **Automation Framework** — YAML-конфиг: контекст, аутентификация, Spider, Active Scan, отчёты. Запуск в CI (Docker): образы `owasp/zap2docker-stable`, `owasp/zap2docker-weekly`; передать конфиг, забрать отчёт.

Пример YAML (минимальный):

```yaml
env:
  configs:
    - name: "default"
      parameters:
        failOnError: false
        progressToStdout: true
jobs:
  - type: "spider"
    parameters:
      url: "https://example.com"
  - type: "activeScan"
    parameters:
      context: "default"
  - type: "report"
    parameters:
      template: "traditional-html"
      reportDir: "/zap/wrk"
      reportFile: "report.html"
```

Запуск: `zap.sh -cmd -autorun /path/to/config.yaml`


## Скрипты (Zest, Python)

- **Zest** — встроенный скриптовый язык; запись через **ZAP → Record** или вручную. Подстановка CSRF-токена, вызов API ZAP, условная логика.
- **Jython (Python 2)** и **Graal.js (JavaScript)** в панели **Scripts**. Типы: Proxy, Stand Alone, **Targeted**, **Active Rule**, **Passive Rule**. Active Rule — своя проверка активного сканера; Passive Rule — своя проверка пассивного (анализ ответа, при необходимости `helper.newAlert()`).


## Отчёты и экспорт

- **Report → Generate HTML Report** — отчёт по алертам (описание, риск, URL, доказательство, рекомендации).
- **Report → Export** — JSON, XML для Jira, GitHub Issues или парсинга в CI.
- В Automation Framework в конфиге задаётся каталог и формат отчёта (HTML, JSON, SARIF); артефакты публикуются в пайплайне.


## Лучшие практики

1. **Context** — задать in-scope URL; не сканировать сторонние домены без разрешения.
2. **Authentication** — настроить в контексте для приложений с логином.
3. **Scan Policy** — отключать деструктивные проверки на продакшене при необходимости; кастомная политика по окружениям.
4. **API key** — задать ключ; не открывать ZAP в сеть без защиты (только localhost или за firewall).
5. **Spider** — ограничивать глубину и узлы; исключать внешние домены и статику при необходимости.
6. **Отчёты** — регулярно экспортировать алерты для трекинга и соответствия (OWASP Top 10, PCI DSS).


## FAQ и решение проблем

### Таблица типичных проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Браузер не открывает страницы через ZAP | Прокси не настроен или порт занят | Local Proxies 8080; в браузере 127.0.0.1:8080 |
| HTTPS не расшифровывается | CA ZAP не установлен | Options → Dynamic SSL Certificates → установить в доверенные |
| Запросы не перехватываются | Выключены Breakpoints или фильтр | Включить Break, проверить настройки перехвата |
| Active Scan не находит уязвимости | Нет контекста/аутентификации | Добавить URL в контекст; настроить Authentication |
| ZAP не стартует | Нет Java или неверная версия | Java 11+; проверить `java -version` |
| API не отвечает | ZAP не запущен или неверный ключ | Запустить ZAP; проверить Options → API |
| Spider не находит страницы | Нужны CSRF/капча | Session Management; скрипты для подстановки CSRF |
| OutOfMemoryError | Много запросов/сессия | Увеличить `-Xmx`; очистить History; перезапуск |

### Краткие ответы

- **ZAP или Burp?** ZAP — бесплатный, встроенный сканер, API для CI. Burp Professional — платный, мощный сканер и Collaborator. Для автоматизации и CI часто ZAP; для ручного тестирования с плагинами — Burp.
- **API с Bearer-токеном?** В контексте **Authentication** — HTTP Header или Session Management с макросом/скриптом, добавляющим `Authorization: Bearer <token>` (токен из запроса логина). Либо вручную заголовок в запросах.
- **ZAP в CI?** Docker-образ ZAP + Automation Framework (YAML): запуск контейнера, передача конфига и URL; по завершении — отчёт в артефактах. Либо headless: `zap.sh -cmd -quickurl ... -quickout ...`.
- **WebSocket?** Да: Options — перехват WebSocket; сообщения в панели **WebSockets**. Активное сканирование WebSocket ограничено; для глубоких проверок — скрипты.
- **Экспорт алертов?** **Report → Generate HTML Report** или **Report → Export** (JSON/XML). Дополнения — SARIF, CSV, интеграция с Jira/GitHub.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **Proxy** | HTTP/HTTPS прокси для перехвата и изменения трафика |
| **Passive scan** | Анализ трафика без дополнительных запросов |
| **Active scan** | Запросы с payload для поиска уязвимостей |
| **Spider** | Обход приложения (ссылки, формы), карта URL |
| **Alert** | Находка сканера (уязвимость или информация) |
| **Context** | Набор URL (in scope) и настроек (аутентификация) |
| **Fuzzer** | Подстановка payload в позиции запроса |
| **Zest** | Скриптовый язык автоматизации в ZAP |
| **Automation Framework** | YAML-конфигурация для автоматизации в CI |
| **Breakpoints** | Остановка запросов/ответов в Proxy для редактирования |
| **Sites** | Дерево хостов и путей по трафику и Spider |
| **Scan Policy** | Правила активного сканирования (набор проверок) |


## Заключение

OWASP ZAP — удобный бесплатный инструмент для ручного и автоматизированного тестирования безопасности веб-приложений: перехват трафика через proxy, пассивное и активное сканирование, Spider, Fuzzer, REST API и Automation Framework для CI/CD. Настройка контекста и аутентификации позволяет тестировать приложения с логином; отчёты и экспорт алертов — интегрировать результаты в процессы разработки и соответствия стандартам.
