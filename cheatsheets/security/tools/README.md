---
title: "Security Tools"
description: "Каталог инструментов безопасности по категориям: SAST, DAST, SCA, secret scanning, container scanning, IAM, WAF и observability."
tags:
  - meta
  - index
  - security
  - tools
type: "index"
aliases:
  - "Security Tools"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Security Tools

Раздел систематизирует инструменты безопасности по категориям: что делает каждый тип, ключевые представители (open source и коммерческие), как выбирать и куда встраивать. Это каталог-навигатор, а не обзор одной тулы: детали применения см. в `../testing/` и в соответствующих разделах `platform/` и `monitoring/`.

Для кого: инженеры и тимлиды, выбирающие стек security-инструментов под свой SDLC, DevOps/SRE, настраивающие сканирование в кластере и CI, и security-чемпионы, которым нужна точка входа в экосистему.

## Полезные ссылки

### Основные документы
- [security-tools](security-tools.md) — обзор категорий и ключевых представителей

### Соседние разделы
- [security/](../../basics/README.md) — корень раздела
- [security/testing/](../../basics/README.md) — методологии тестирования
- [security/infrastructure/](../../basics/README.md) — кластер и облако
- [monitoring/](../../basics/README.md) — наблюдаемость и SIEM
- [platform/ci-cd/](../../basics/README.md) — интеграция в пайплайн

### Внешние ресурсы
- [OWASP Source Code Analysis Tools](https://owasp.org/www-community/Source_Code_Analysis_Tools)
- [OWASP Vulnerability Scanning Tools](https://owasp.org/www-community/Vulnerability_Scanning_Tools)
- [OWASP ZAP](https://www.zaproxy.org/), [Semgrep](https://semgrep.dev/), [Trivy](https://trivy.dev/)
- [SonarQube](https://www.sonarqube.org/), [Snyk](https://snyk.io/)
- [Falco](https://falco.org/) — runtime-security для контейнеров

## Содержание

- [Карта категорий](#карта-категорий)
- [Open Source vs коммерческие](#open-source-vs-коммерческие)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта категорий

| Категория | Зачем | Примеры |
|-----------|-------|---------|
| SAST | Поиск уязвимостей в коде | Semgrep, SonarQube, Checkmarx |
| DAST | Тестирование запущенного приложения | OWASP ZAP, Burp Suite |
| SCA | Зависимости и лицензии | Snyk, Dependency-Check, Renovate |
| Container scanning | CVE в образах | Trivy, Grype, Clair |
| IaC scanning | Ошибки в Terraform/K8s-манифестах | tfsec, Checkov, kube-bench |
| Secret scanning | Токены в репозитории | Gitleaks, TruffleHog |
| WAF | Web Application Firewall | ModSecurity, Cloudflare, AWS WAF |
| Runtime security | Аномалии в рантайме | Falco, Tetragon |
| SIEM / SOAR | Коррелирование событий | Wazuh, Splunk, Elastic SIEM |
| IAM / PAM | Управление доступом | Vault, Teleport, CyberArk |

## Open Source vs коммерческие

- **Open source** (Semgrep, ZAP, Trivy, Falco) — достаточно для 80% задач у среднего проекта, легко встроить в CI, нет проблем с лицензированием.
- **Коммерческие** (Snyk, Checkmarx, Veracode, Prisma) — выигрывают по UI, компланс-отчётам, интеграциям и поддержке. Оправданы при регулируемых требованиях (SOC2, PCI DSS) или больших командах.
- **Гибрид** — частый выбор: open source как первый барьер в CI + коммерческая платформа для агрегации и отчётности.

## Когда использовать

- Стартап/небольшой проект — open source + один security-dashboard.
- Компания с compliance — коммерческая SCA + SAST + DAST с отчётностью под аудит.
- Kubernetes-platform team — Trivy + Falco + OPA/Kyverno обязательны.
- Финтех/критичная инфра — добавляются WAF, SIEM и регулярные пентесты.

## Маршруты чтения

- **Выбор стека для команды:** `security-tools.md` категория за категорией сопоставить с бюджетом и compliance.
- **Настройка пайплайна:** `security-tools.md` + `../testing/security-testing.md` + `../../platform/ci-cd/`.
- **Runtime-мониторинг кластера:** категории Container scanning + Runtime security + SIEM.

## Куда идти дальше

- Методики тестирования — [README](../../basics/README.md)
- Инфраструктурный hardening — [README](../../basics/README.md)
- Secrets и vault — [secrets-management](../data/secrets-management.md)
- Observability и SIEM — [README](../../basics/README.md)
