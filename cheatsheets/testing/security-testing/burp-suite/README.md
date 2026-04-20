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
- [[burp-suite|Burp Suite]] — Proxy, Scanner, Repeater, Intruder, расширения

### Соседние разделы
- [[README|Security Testing]]
- [[README|OWASP ZAP]]
- [[README|sqlmap]]
- [[testing-tools-overview|Testing Tools Overview]]
- [[README|Security]]

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
| Proxy: перехват, модификация, HTTPS | [[burp-suite]] |
| Scanner: passive/active crawl | [[burp-suite]] |
| Repeater и Intruder (фаззинг, перебор) | [[burp-suite]] |
| Decoder, Comparer, BApp-расширения | [[burp-suite]] |
| Отчёты, CI-интеграция (Enterprise) | [[burp-suite]] |

## Когда использовать

- **Burp vs OWASP ZAP** — Burp Professional сильнее в Scanner и UX, ZAP полностью бесплатный и лучше автоматизируется из коробки.
- **Burp vs sqlmap** — Burp универсален (XSS, IDOR, SSRF и т. д.), sqlmap специализирован на SQL-инъекциях.
- **Ручной пентест** — Repeater + Intruder + Decoder; **DAST в CI** — Enterprise или ZAP.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение → Proxy + browser → Repeater → Intruder.
- **Пентест веб-приложения (1 день):** весь документ + Web Security Academy labs.

## Куда идти дальше

- Обзор security testing — [[README]]
- Open-source альтернатива — [[README]]
- Специализированный SQLi — [[README]]
- Безопасность приложений — [[README]]
