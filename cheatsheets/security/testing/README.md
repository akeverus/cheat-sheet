---
title: "Security Testing"
description: "SAST, DAST, SCA, IAST и пентесты — как встроить тестирование безопасности в SDLC и CI/CD."
tags:
  - meta
  - index
  - security
  - testing
type: "index"
updated: "2026-04-17"
---
# Security Testing

Раздел описывает, как и чем тестировать безопасность: статический анализ кода (SAST), динамическое тестирование запущенного приложения (DAST), анализ зависимостей на CVE (SCA), гибридный IAST, а также ручные пентесты и bug bounty. Главный акцент — интеграция в CI/CD так, чтобы не ломать скорость поставки.

Для кого: разработчики, QA и security-инженеры, которые встраивают проверки в пайплайн, отбирают инструменты и интерпретируют результаты. Конкретные инструменты (OWASP ZAP, Semgrep, Snyk, Trivy и т.д.) каталогизированы в `../tools/`.

## Полезные ссылки

### Основные документы
- [[security-testing]] — подходы SAST/DAST/SCA/IAST и их интеграция в пайплайн

### Соседние разделы
- [[README|security/]] — корень раздела
- [[README|security/tools/]] — конкретные инструменты
- [[README|security/application/]] — что именно тестируем (OWASP Top 10)
- [[README|platform/ci-cd/]] — куда встраивать проверки
- [[README|testing/]] — общий раздел тестирования

### Внешние ресурсы
- [OWASP Web Security Testing Guide (WSTG)](https://owasp.org/www-project-web-security-testing-guide/)
- [OWASP ZAP](https://www.zaproxy.org/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [OWASP SAMM](https://owaspsamm.org/) — зрелость процессов security
- [Semgrep](https://semgrep.dev/), [Snyk](https://snyk.io/), [Trivy](https://trivy.dev/)

## Содержание

- [Карта тем](#карта-тем)
- [SAST vs DAST vs SCA vs IAST](#sast-vs-dast-vs-sca-vs-iast)
- [Когда что использовать](#когда-что-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Где читать |
|------|-----------|
| SAST (статический анализ) | [[security-testing]] |
| DAST (динамическое тестирование) | [[security-testing]] |
| SCA (зависимости, CVE) | [[security-testing]] |
| IAST и RASP | [[security-testing]] |
| Secret scanning | [[security-testing]] |
| Container scanning | [[security-testing]] |
| Fuzzing | [[security-testing]] |
| Пентесты и bug bounty | [[security-testing]] |

## SAST vs DAST vs SCA vs IAST

| Подход | Когда срабатывает | Что видит | Типичный инструмент |
|--------|-------------------|-----------|---------------------|
| SAST | На коде до сборки | Уязвимые паттерны в исходнике | Semgrep, SonarQube, Checkmarx |
| DAST | На запущенном приложении | Реальные ответы HTTP, окружение | OWASP ZAP, Burp |
| SCA | На зависимостях | Известные CVE в библиотеках | Snyk, Dependency-Check, Trivy |
| IAST | Рантайм + агент в приложении | SAST-точность + реальный трафик | Contrast, Seeker |

SAST даёт много false positive, но ловит проблемы рано; DAST ловит то, что реально эксплуатируется, но требует окружения; SCA — самое дешёвое и обязательное; IAST дорогой и требует интеграции.

## Когда что использовать

- **SAST** — на каждом PR, блокировать по high/critical. Для больших монорепо — инкрементально.
- **SCA** — на каждом PR + ежедневный скан зависимостей (появляются новые CVE).
- **DAST** — на staging/pre-prod перед релизом, полный прогон раз в неделю.
- **Secret scanning** — pre-commit hook + pre-push + CI.
- **Container scanning** — на каждой сборке образа.
- **Пентест** — раз в год и при крупных изменениях архитектуры.

## Маршруты чтения

- **Начало для команды без security-процессов:** `security-testing.md` → включите SCA + secret scanning → дальше SAST → DAST.
- **Перед ISO/SOC 2 аудитом:** `security-testing.md` + `../security-practices.md` + ownership по процессам.
- **Разбор инцидента:** `../application/` + `../testing/security-testing.md` для выбора следующих проверок.

## Куда идти дальше

- Инструменты и их сравнение — [[README]]
- Что именно проверяем (OWASP Top 10) — [[README]]
- Встраивание в пайплайн — [[README]]
- Общие подходы к тестированию — [[README]]
