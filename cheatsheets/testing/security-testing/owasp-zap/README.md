---
title: "OWASP ZAP"
description: "Точка входа в раздел OWASP ZAP: открытый proxy и сканер безопасности веб-приложений."
tags:
  - meta
  - index
  - testing
  - security-testing
  - owasp-zap
type: "index"
aliases:
  - "OWASP ZAP"
prerequisites: []
next: []
updated: "2026-04-20"
---
# OWASP ZAP

OWASP ZAP (Zed Attack Proxy) — открытый инструмент тестирования безопасности веб-приложений от OWASP Foundation. Работает как proxy (перехват трафика), включает пассивный и активный Scanner, Fuzzer, REST API для автоматизации, скрипты (Zest, Python, Groovy), Automation Framework и готовые Docker-образы для CI/CD.

Применяйте, когда нужен бесплатный и полностью автоматизируемый DAST-инструмент в пайплайне, или при пентесте веб-приложения без бюджета на Burp Professional. ZAP хорош для shift-left security: baseline scan и full scan легко встраиваются в GitLab CI / Jenkins / GitHub Actions.

**Важно:** любое сканирование и фаззинг проводите **только с письменного разрешения владельца системы**. Сканирование чужих систем без согласования может нарушать закон.

## Полезные ссылки

### Основные документы
- [OWASP ZAP](owasp-zap.md) — Proxy, Scanner, Fuzzer, API, Automation Framework

### Соседние разделы
- [Security Testing](../../../basics/README.md)
- [Burp Suite](../../../basics/README.md)
- [sqlmap](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)
- [Security](../../../basics/README.md)

### Внешние ресурсы
- [zaproxy.org](https://www.zaproxy.org/)
- [Getting Started](https://www.zaproxy.org/docs/getting-started/)
- [Automation Framework](https://www.zaproxy.org/docs/automate/automation-framework/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Proxy: перехват, HTTPS-сертификат | [owasp-zap](owasp-zap.md) |
| Scanner: passive / active, policies | [owasp-zap](owasp-zap.md) |
| Fuzzer, payloads | [owasp-zap](owasp-zap.md) |
| REST API, Automation Framework | [owasp-zap](owasp-zap.md) |
| Скрипты Zest/Python, отчёты | [owasp-zap](owasp-zap.md) |

## Когда использовать

- **ZAP vs Burp Suite** — ZAP полностью бесплатный и открытый, лучше автоматизируется; Burp Professional сильнее в UX и Scanner для ручной работы.
- **ZAP vs sqlmap** — ZAP универсальный DAST (XSS, SSRF, IDOR и т. д.); sqlmap — специализирован на SQL-инъекциях.
- **CI/CD** — ZAP официально поставляется как Docker-образ с baseline / full / API scan.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение Proxy + browser passive scan отчёт.
- **DAST в CI (1 день):** весь документ + Automation Framework + Docker baseline scan в GitLab/Jenkins.

## Куда идти дальше

- Обзор security testing — [README](../../../basics/README.md)
- Коммерческая альтернатива — [README](../../../basics/README.md)
- Специализированный SQLi — [README](../../../basics/README.md)
