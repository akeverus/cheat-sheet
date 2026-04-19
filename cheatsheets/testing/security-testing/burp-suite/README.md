---
title: "Burp Suite"
description: "Точка входа в раздел Burp Suite: платформа для ручного и автоматизированного тестирования безопасности веб-приложений."
tags:
  - meta
  - index
  - testing
  - security-testing
  - burp-suite
type: "index"
updated: "2026-04-17"
---
# Burp Suite

Burp Suite от PortSwigger — промышленная платформа для тестирования безопасности веб-приложений. Включает Proxy (перехват и модификация трафика), Scanner (пассивное и активное сканирование), Repeater (ручная отправка запросов), Intruder (фаззинг и перебор), Decoder, Comparer, расширения (BApp Store) и CI-интеграцию в Enterprise-редакции.

Применяйте при ручных пентестах, ассессментах веб-приложений и API, а также для автоматического сканирования в рамках SDLC. Community Edition подходит для ручной работы, Professional и Enterprise — для Scanner и CI/CD.

**Важно:** любое тестирование безопасности должно проводиться **только с письменного разрешения владельца системы** (ROE, scope, авторизация). Активное сканирование и фаззинг без разрешения могут нарушать законодательство и условия эксплуатации.

## Полезные ссылки

### Основные документы
- [Burp Suite](burp-suite.md) — Proxy, Scanner, Repeater, Intruder, расширения

### Соседние разделы
- [Security Testing](../README.md)
- [OWASP ZAP](../owasp-zap/README.md)
- [sqlmap](../sqlmap/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)
- [Security](../../../security/README.md)

### Внешние ресурсы
- [Burp Suite Documentation](https://portswigger.net/burp/documentation)
- [PortSwigger Web Security Academy](https://portswigger.net/web-security)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Proxy: перехват, модификация, HTTPS | [burp-suite.md](burp-suite.md) |
| Scanner: passive/active crawl | [burp-suite.md](burp-suite.md) |
| Repeater и Intruder (фаззинг, перебор) | [burp-suite.md](burp-suite.md) |
| Decoder, Comparer, BApp-расширения | [burp-suite.md](burp-suite.md) |
| Отчёты, CI-интеграция (Enterprise) | [burp-suite.md](burp-suite.md) |

## Когда использовать

- **Burp vs OWASP ZAP** — Burp Professional сильнее в Scanner и UX, ZAP полностью бесплатный и лучше автоматизируется из коробки.
- **Burp vs sqlmap** — Burp универсален (XSS, IDOR, SSRF и т. д.), sqlmap специализирован на SQL-инъекциях.
- **Ручной пентест** — Repeater + Intruder + Decoder; **DAST в CI** — Enterprise или ZAP.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение → Proxy + browser → Repeater → Intruder.
- **Пентест веб-приложения (1 день):** весь документ + Web Security Academy labs.

## Куда идти дальше

- Обзор security testing — [../README.md](../README.md)
- Open-source альтернатива — [../owasp-zap/README.md](../owasp-zap/README.md)
- Специализированный SQLi — [../sqlmap/README.md](../sqlmap/README.md)
- Безопасность приложений — [../../../security/README.md](../../../security/README.md)
