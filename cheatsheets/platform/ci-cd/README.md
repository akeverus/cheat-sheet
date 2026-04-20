---
title: "CI/CD"
description: "Индекс CI/CD материалов: инструменты, маршрут запуска pipeline с нуля и миграция с legacy-решений."
tags:
  - meta
  - index
type: "index"
updated: "2026-04-20"
---
# CI/CD

Индекс материалов по непрерывной интеграции и доставке.

## Полезные ссылки

[jenkins](jenkins.md)
[GitLab CI](gitlab-ci.md)
[Azure DevOps](azure-devops.md)

## Содержание

- [Быстрый маршрут: pipeline с нуля](#быстрый-маршрут-pipeline-с-нуля)
- [Legacy: миграция с Travis CI](#legacy-миграция-с-travis-ci)
- [См. также](#см-также)

## Быстрый маршрут: pipeline с нуля

1. Выбрать платформу (`GitHub Actions` или `GitLab CI`) и описать минимальный pipeline: `build -> test`.
2. Добавить кэш зависимостей и артефакты сборки.
3. Подключить quality gates: линтер, unit/integration тесты, security scan.
4. Настроить деплой (staging -> production) и стратегию релизов (rolling/canary/blue-green).
5. Добавить наблюдаемость pipeline: время стадий, flaky jobs, fail-rate и алерты.

## Legacy: миграция с Travis CI

- `Travis CI` в 2026 рассматривается как legacy-вариант; для новых проектов предпочтительны `GitHub Actions` или `GitLab CI`.
- Перед миграцией зафиксировать эквиваленты: матрица версий, секреты, кэширование, release-джобы.
- Детальный контекст и ограничения: [travis-ci](travis-ci.md).

## См. также

- [travis-ci.md](travis-ci.md) — исторический контекст и оговорки по Travis CI.
- [github-actions.md](github-actions.md) — практики для современного workflow в GitHub.
- [gitlab-ci.md](gitlab-ci.md) — альтернативный CI/CD контур для GitLab.
