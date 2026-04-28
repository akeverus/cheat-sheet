---
title: "sqlmap"
description: "Точка входа в раздел sqlmap: автоматическое обнаружение и эксплуатация SQL-инъекций."
tags:
  - meta
  - index
  - testing
  - security-testing
  - sqlmap
type: "index"
aliases:
  - "sqlmap"
prerequisites: []
next: []
updated: "2026-04-20"
---
# sqlmap

sqlmap — открытый инструмент для автоматического тестирования на SQL-инъекции. Поддерживает обнаружение уязвимостей, извлечение данных, чтение/запись файлов, выполнение команд ОС при подходящих условиях, обход WAF через tamper-скрипты, параметры в GET/POST/Cookie/Header, интеграцию с Burp и OWASP ZAP, CI-сценарии и ручной режим.

Применяйте для проверки конкретной гипотезы об SQLi: подозрительный параметр запустить sqlmap получить подтверждение и PoC. Для широкого DAST-покрытия используйте Burp/ZAP, а sqlmap — как специализированный инструмент под один класс уязвимостей.

**Важно:** использование sqlmap против чужих систем **без письменного разрешения владельца системы незаконно**. Инструмент может извлекать данные и исполнять команды — всегда работайте в согласованном scope с явным authorization letter.

## Полезные ссылки

### Основные документы
- [sqlmap](sqlmap.md) — обнаружение, извлечение, tamper, обход WAF

### Соседние разделы
- [Security Testing](../../../basics/README.md)
- [Burp Suite](../../../basics/README.md)
- [OWASP ZAP](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)
- [Security](../../../basics/README.md)

### Внешние ресурсы
- [sqlmap Official](https://sqlmap.org/)
- [sqlmap Wiki](https://github.com/sqlmapproject/sqlmap/wiki)
- [OWASP: SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Установка, базовые команды | [sqlmap](sqlmap.md) |
| Параметры GET/POST/Cookie/Header | [sqlmap](sqlmap.md) |
| Извлечение данных: `--dbs`, `--tables`, `--dump` | [sqlmap](sqlmap.md) |
| Tamper-скрипты и обход WAF | [sqlmap](sqlmap.md) |
| Интеграция с Burp / ZAP, CI-сценарии | [sqlmap](sqlmap.md) |

## Когда использовать

- **sqlmap vs Burp Scanner** — Burp Scanner даёт широкий surface (XSS, SSRF и т. д.), sqlmap глубже в SQLi: tamper, DBMS fingerprint, OOB-техники.
- **sqlmap vs ручной SQLi** — ручная работа нужна для нестандартных стеков и второго порядка; sqlmap автоматизирует проверку стандартных случаев.
- **CI** — осторожно; активное извлечение лучше держать вне автоматических пайплайнов и использовать явный scope.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение базовый запуск на тестовом URL `--dbs` `--dump` на локальной мишени (DVWA/juice-shop).
- **Пентест (1 день):** весь документ + tamper-скрипты + интеграция с Burp.

## Куда идти дальше

- Обзор security testing — [README](../../../basics/README.md)
- Универсальный DAST — [README](../../../basics/README.md)
- Платформенный пентест — [README](../../../basics/README.md)
- OWASP Top 10 — [README](../../../basics/README.md)
