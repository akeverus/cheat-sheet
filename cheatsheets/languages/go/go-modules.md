---
title: "Go: модули"
description: "Полное руководство по модулям в Go: go.mod, go.sum, зависимости, версионирование, vendoring"
tags:
  - go
  - golang
  - modules
  - dependencies
  - packages
type: "overview"
difficulty: "intermediate"
aliases:
  - "Go"
  - "модули"
  - "Go: модули"
  - "go modules"
prerequisites:
  - "[[go-basics]]"
next: []
updated: "2026-04-20"
---

# Go: модули


### См. также
- [Вопросы на собеседовании](../../interview/programming-languages/go/go-modules-interview.md) — подготовка к интервью

## Полезные ссылки

- [Go Modules Documentation](https://go.dev/ref/mod)
- [Go Modules Wiki](https://github.com/golang/go/wiki/Modules)

## Содержание

- [Введение в модули](#введение-в-модули)
  - [Основные концепции](#основные-концепции)
- [Создание модуля](#создание-модуля)
  - [Инициализация модуля](#инициализация-модуля)
  - [Структура go.mod](#структура-gomod)
- [Управление зависимостями](#управление-зависимостями)
  - [Добавление зависимостей](#добавление-зависимостей)
  - [Обновление зависимостей](#обновление-зависимостей)
  - [Удаление зависимостей](#удаление-зависимостей)
- [Версионирование](#версионирование)
  - [Семантическое версионирование](#семантическое-версионирование)
  - [Версии модулей](#версии-модулей)
  - [Псевдо-версии](#псевдо-версии)
- [Работа с go.mod](#работа-с-gomod)
  - [Редактирование go.mod](#редактирование-gomod)
  - [Просмотр зависимостей](#просмотр-зависимостей)
  - [Проверка зависимостей](#проверка-зависимостей)
- [Vendoring](#vendoring)
  - [Создание vendor директории](#создание-vendor-директории)
  - [Структура vendor](#структура-vendor)
  - [Детальная работа с go.mod](#детальная-работа-с-gomod)
  - [Работа с приватными репозиториями](#работа-с-приватными-репозиториями)
  - [Версионирование модулей](#версионирование-модулей)
  - [Миграция на новую версию модуля](#миграция-на-новую-версию-модуля)
  - [Работа с псевдо-версиями](#работа-с-псевдо-версиями)
  - [Практические примеры: Многоуровневые модули](#практические-примеры-многоуровневые-модули)
  - [Практические примеры: Replace для локальной разработки](#практические-примеры-replace-для-локальной-разработки)
  - [Практические примеры: Работа с форками](#практические-примеры-работа-с-форками)
  - [Практические примеры: Управление зависимостями в CI/CD](#практические-примеры-управление-зависимостями-в-cicd)
  - [Практические примеры: Обновление зависимостей](#практические-примеры-обновление-зависимостей)
  - [Практические примеры: Анализ зависимостей](#практические-примеры-анализ-зависимостей)
  - [Практические примеры: Работа с vendor](#практические-примеры-работа-с-vendor)
  - [Практические примеры: Миграция с GOPATH](#практические-примеры-миграция-с-gopath)
  - [Практические примеры: Работа с workspace mode](#практические-примеры-работа-с-workspace-mode)
  - [Практические примеры: Управление версиями Go](#практические-примеры-управление-версиями-go)
  - [Практические примеры: Работа с тегами](#практические-примеры-работа-с-тегами)
  - [Практические примеры: Безопасность зависимостей](#практические-примеры-безопасность-зависимостей)
  - [Практические примеры: Оптимизация размера зависимостей](#практические-примеры-оптимизация-размера-зависимостей)
  - [Практические примеры: Работа с replace в команде](#практические-примеры-работа-с-replace-в-команде)
  - [Практические примеры: Автоматическое управление зависимостями](#практические-примеры-автоматическое-управление-зависимостями)
  - [Практические примеры: Работа с приватными модулями](#практические-примеры-работа-с-приватными-модулями)
  - [Практические примеры: Обновление зависимостей](#практические-примеры-обновление-зависимостей-1)
  - [Практические примеры: Управление зависимостями в CI/CD](#практические-примеры-управление-зависимостями-в-cicd-1)
  - [Практические примеры: Локальная разработка с replace](#практические-примеры-локальная-разработка-с-replace)
  - [Практические примеры: Версионирование модуля](#практические-примеры-версионирование-модуля)
  - [Практические примеры: Проверка безопасности зависимостей](#практические-примеры-проверка-безопасности-зависимостей)
  - [Практические примеры: Работа с workspace (Go 1.18+)](#практические-примеры-работа-с-workspace-go-118)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также-1)

## Введение в модули

Go **Modules** — это система управления зависимостями, введенная в `Go 1.11`. Модули позволяют управлять версиями зависимостей и обеспечивают воспроизводимые сборки.

### Основные концепции

1. **Модуль** — коллекция Go пакетов с версией
2. **go.mod** — файл описания модуля
3. **go.sum** — файл с контрольными суммами зависимостей
4. **Версионирование** — семантическое версионирование (semver)

## Создание модуля

### Инициализация модуля

```bash
# Создание нового модуля
go mod init example.com/myproject

# Инициализация с версией
go mod init example.com/myproject/v2
```

### Структура go.mod

```go
module example.com/myproject

go 1.21

require (
    github.com/gin-gonic/gin v1.9.1
    github.com/lib/pq v1.10.9
)

require (
    github.com/bytedance/sonic v1.9.1 // indirect
)
```

## Управление зависимостями

### Добавление зависимостей

```bash
# Добавление зависимости
go get github.com/gin-gonic/gin

# Добавление конкретной версии
go get github.com/gin-gonic/gin@v1.9.1

# Добавление последней версии
go get github.com/gin-gonic/gin@latest

# Добавление версии из ветки
go get github.com/gin-gonic/gin@master
```

### Обновление зависимостей

```bash
# Обновление всех зависимостей
go get -u ./...

# Обновление конкретной зависимости
go get -u github.com/gin-gonic/gin

# Обновление до последней версии
go get -u github.com/gin-gonic/gin@latest
```

### Удаление зависимостей

```bash
# Удаление неиспользуемых зависимостей
go mod tidy

# Удаление конкретной зависимости
go mod edit -droprequire github.com/gin-gonic/gin
```

## Версионирование

### Семантическое версионирование

**Go использует семантическое версионирование (semver):**
- **MAJOR.`MINOR`.PATCH** (например, `v1.2`.3)
- **MAJOR** — несовместимые изменения **API**
- **MINOR** — обратно совместимые новые функции
- **PATCH** — обратно совместимые исправления ошибок

### Версии модулей

```go
// Основная версия
module example.com/myproject

// Версия 2
module example.com/myproject/v2

// Версия 3
module example.com/myproject/v3
```

### Псевдо-версии

```go
// Псевдо-версия для коммитов
require example.com/mymodule v0.0.0-20230101120000-abcdef123456
```

## Работа с go.mod

### Редактирование go.mod

```bash
# Добавление require
go mod edit -require github.com/gin-gonic/gin@v1.9.1

# Удаление require
go mod edit -droprequire github.com/gin-gonic/gin

# Замена модуля
go mod edit -replace example.com/old=example.com/new@v1.0.0

# Удаление replace
go mod edit -dropreplace example.com/old
```

### Просмотр зависимостей

```bash
# Список всех зависимостей
go list -m all

# Список прямых зависимостей
go list -m -f '{{if not .Indirect}}{{.}}{{end}}' all

# Информация о модуле
go list -m -versions github.com/gin-gonic/gin
```

### Проверка зависимостей

```bash
# Проверка зависимостей
go mod verify

# Загрузка зависимостей
go mod download

# Синхронизация зависимостей
go mod tidy
```

## Vendoring

**Vendoring** позволяет включить зависимости в репозиторий проекта.

### Создание vendor директории

```bash
# Создание vendor
go mod vendor

# Использование vendor при сборке
go build -mod=vendor

# Использование vendor при тестах
go test -mod=vendor
```

### Структура vendor

```text
vendor/
├── github.com/
│   └── gin-gonic/
│       └── gin/
│           └── ...
└── modules.txt
```

### Детальная работа с go.mod

```bash
# Просмотр текущего go.mod
cat go.mod

# Редактирование go.mod вручную
go mod edit -module example.com/newmodule

# Изменение версии Go
go mod edit -go=1.21

# Добавление require с комментарием
go mod edit -require=github.com/gin-gonic/gin@v1.9.1
go mod edit -require=github.com/lib/pq@v1.10.9

# Добавление exclude
go mod edit -exclude=github.com/old/module@v1.0.0

# Добавление replace для локальной разработки
go mod edit -replace=example.com/module=../local-module

# Добавление replace для форка
go mod edit -replace=github.com/original/repo=github.com/myfork/repo@v1.0.0
```

### Работа с приватными репозиториями

```bash
# Настройка GOPRIVATE
go env -w GOPRIVATE=example.com,*.example.com

# Настройка GOPROXY для приватных модулей
go env -w GOPROXY=https://proxy.golang.org,direct
go env -w GOSUMDB=sum.golang.org

# Настройка для корпоративного прокси
go env -w GOPROXY=https://corp-proxy.example.com
go env -w GOSUMDB=off
```

### Версионирование модулей

```go
// Основная версия (v0 или v1)
module example.com/myproject

// Версия 2 (требует изменения импортов)
module example.com/myproject/v2

// Версия 3
module example.com/myproject/v3
```

### Миграция на новую версию модуля

```go
// Старый импорт
import "example.com/myproject"

// Новый импорт для v2
import "example.com/myproject/v2"
```

### Работа с псевдо-версиями

```bash
# Использование конкретного коммита
go get example.com/module@abc123def456

# Использование ветки
go get example.com/module@master

# Использование тега
go get example.com/module@v1.2.3
```

### Практические примеры: Многоуровневые модули

```go
// Корневой модуль
module example.com/monorepo

// Подмодуль
module example.com/monorepo/api

// Другой подмодуль
module example.com/monorepo/utils
```

```go
// Использование подмодулей
import "example.com/monorepo/api"
import "example.com/monorepo/utils"
```

### Практические примеры: Replace для локальной разработки

```go
// go.mod
module example.com/myapp

require (
    example.com/shared v1.0.0
)

replace example.com/shared => ../shared
```

```bash
# Временная замена для тестирования
go mod edit -replace=example.com/shared=../shared-fork
go mod tidy
go test ./...

# Откат замены
go mod edit -dropreplace=example.com/shared
go mod tidy
```

### Практические примеры: Работа с форками

```go
// go.mod
module example.com/myapp

require (
    github.com/original/repo v1.0.0
)

replace github.com/original/repo => github.com/myfork/repo v1.0.0
```

### Практические примеры: Управление зависимостями в `CI/CD`

```yaml
# GitHub Actions
- name: Setup Go
  uses: actions/setup-go@v4
  with:
    go-version: '1.21'

- name: Cache Go modules
  uses: actions/cache@v3
  with:
    path: ~/go/pkg/mod
    key: ${{ runner.os }}-go-${{ hashFiles('/go.sum') }}

- name: Download dependencies
  run: go mod download

- name: Verify dependencies
  run: go mod verify

- name: Tidy dependencies
  run: go mod tidy

- name: Check for changes
  run: |
    git diff --exit-code go.mod go.sum || (echo "Dependencies changed" && exit 1)
```

### Практические примеры: Обновление зависимостей

```bash
#!/bin/bash
# Скрипт для безопасного обновления зависимостей

# Создание резервной копии
cp go.mod go.mod.backup
cp go.sum go.sum.backup

# Обновление всех зависимостей
go get -u ./...

# Запуск тестов
go test ./...

# Если тесты прошли, удаляем backup
if [ $? -eq 0 ]; then
    rm go.mod.backup go.sum.backup
    echo "Dependencies updated successfully"
else
    # Откат при ошибке
    mv go.mod.backup go.mod
    mv go.sum.backup go.sum
    echo "Update failed, reverted changes"
    exit 1
fi
```

### Практические примеры: Анализ зависимостей

```bash
# Список всех зависимостей
go list -m all

# Список прямых зависимостей
go list -m -f '{{if not .Indirect}}{{.}}{{end}}' all

# Список косвенных зависимостей
go list -m -f '{{if .Indirect}}{{.}}{{end}}' all

# Дерево зависимостей
go mod graph

# Почему используется зависимость
go mod why -m github.com/gin-gonic/gin

# Информация о модуле
go list -m -versions github.com/gin-gonic/gin
go list -m -json github.com/gin-gonic/gin
```

### Практические примеры: Работа с vendor

```bash
# Создание vendor директории
go mod vendor

# Проверка vendor
go mod vendor -v

# Использование vendor при сборке
go build -mod=vendor

# Использование vendor при тестах
go test -mod=vendor ./...

# Использование vendor везде
go env -w GOFLAGS=-mod=vendor
```

### Практические примеры: Миграция с GOPATH

```bash
# Старый способ (GOPATH)
export GOPATH=$HOME/go
export PATH=$PATH:$GOPATH/bin

# Новый способ (Modules)
# GOPATH больше не нужен для модулей
# Просто работайте в любой директории
cd ~/myproject
go mod init example.com/myproject
```

### Практические примеры: Работа с workspace mode

```bash
# Создание workspace
go work init ./module1 ./module2

# Добавление модуля в workspace
go work use ./module3

# Удаление модуля из workspace
go work use -r ./module3

# Просмотр workspace
cat go.work
```

```go
// go.work
go 1.21

use (
    ./module1
    ./module2
)
```

### Практические примеры: Управление версиями Go

```go
// go.mod
module example.com/myproject

go 1.21

// Минимальная версия Go для модуля
```

```bash
# Проверка версии Go
go version

# Обновление версии в go.mod
go mod edit -go=1.22

# Проверка совместимости
go mod verify
```

### Практические примеры: Работа с тегами

```bash
# Создание тега для версии
git tag v1.0.0
git push origin v1.0.0

# Использование тега в зависимостях
go get example.com/module@v1.0.0

# Список доступных тегов
git ls-remote --tags origin
```

### Практические примеры: Безопасность зависимостей

```bash
# Проверка уязвимостей
go list -json -m all | grep -i vulnerability

# Обновление для исправления уязвимостей
go get -u github.com/vulnerable/package@latest

# Использование go-audit или других инструментов
```

### Практические примеры: Оптимизация размера зависимостей

```bash
# Анализ размера зависимостей
go list -m -f '{{.Path}} {{.Size}}' all

# Удаление неиспользуемых зависимостей
go mod tidy

# Проверка неиспользуемых зависимостей
go mod why -m all
```

### Практические примеры: Работа с replace в команде

```go
// go.mod для разработки
module example.com/myapp

replace example.com/shared => ../shared

// .gitignore
# Игнорируем локальные replace
# Но коммитим go.mod с replace для команды
```

### Практические примеры: Автоматическое управление зависимостями

```go
// tools.go для dev зависимостей
//go:build tools
// +build tools

package tools

import (
    _ "github.com/golangci/golangci-lint/cmd/golangci-lint"
    _ "golang.org/x/tools/cmd/goimports"
)
```

```bash
# Установка dev зависимостей
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest
go install golang.org/x/tools/cmd/goimports@latest
```

### Практические примеры: Работа с приватными модулями

```bash
# Настройка для приватных репозиториев
export GOPRIVATE=gitlab.com/mycompany,github.com/myorg
export GONOPROXY=gitlab.com/mycompany,github.com/myorg
export GONOSUMDB=gitlab.com/mycompany,github.com/myorg

# Настройка Git для аутентификации
git config --global url."git@gitlab.com:".insteadOf "https://gitlab.com/"
```

### Практические примеры: Обновление зависимостей

```bash
# Обновление всех зависимостей до последних минорных версий
go get -u ./...

# Обновление конкретной зависимости
go get -u github.com/example/package@latest

# Обновление до конкретной версии
go get github.com/example/package@v1.2.3

# Обновление до последней версии с игнором минорных версий
go get -u=patch ./...

# Просмотр доступных версий
go list -m -versions github.com/example/package
```

### Практические примеры: Управление зависимостями в `CI/CD`

```yaml
# GitHub Actions
- name: Setup Go
  uses: actions/setup-go@v4
  with:
    go-version: '1.21'
    cache-dependency-path: go.sum

- name: Download dependencies
  run: go mod download

- name: Verify dependencies
  run: go mod verify

- name: Check for updates
  run: |
    go list -m -u all
    go get -u ./... || true
    go mod tidy
```

### Практические примеры: Локальная разработка с replace

```go
// go.mod
module example.com/myapp

go 1.21

require (
    github.com/example/package v1.0.0
)

// Замена на локальную версию
replace github.com/example/package => ../local-package

// Замена на конкретную версию
replace github.com/example/package => github.com/example/package v1.1.0

// Замена на fork
replace github.com/example/package => github.com/myfork/package v1.0.0
```

### Практические примеры: Версионирование модуля

```bash
# Создание тега версии
git tag v1.0.0
git push origin v1.0.0

# Создание минорной версии
git tag v1.1.0
git push origin v1.1.0

# Создание мажорной версии (breaking changes)
git tag v2.0.0
git push origin v2.0.0

# В go.mod для v2+ модулей нужно указать путь
module example.com/mymodule/v2
```

### Практические примеры: Проверка безопасности зависимостей

```bash
# Использование govulncheck
go install golang.org/x/vuln/cmd/govulncheck@latest
govulncheck ./...

# Использование nancy
go install github.com/sonatypecommunity/nancy@latest
go list -json -deps | nancy sleuth

# Использование gosec
go install github.com/securego/gosec/v2/cmd/gosec@latest
gosec ./...
```

### Практические примеры: Работа с workspace (Go 1.18+)

```bash
# Создание workspace
go work init
go work use ./module1
go work use ./module2

# go.work файл
go 1.21

use (
    ./module1
    ./module2
)

# Обновление зависимостей в workspace
go work sync
```

## Лучшие практики

1. **Используйте семантическое версионирование** — следуйте **semver** для версий модулей
2. **Коммитьте `go.mod` и go.sum** — включайте эти файлы в репозиторий
3. **Используйте go mod tidy** — регулярно очищайте неиспользуемые зависимости
4. **Фиксируйте версии** — используйте конкретные версии в **production**
5. **Обновляйте зависимости** — регулярно обновляйте зависимости для безопасности
6. **Используйте replace** — для локальной разработки и тестирования
7. **Проверяйте зависимости** — используйте go **mod verify** для проверки целостности
8. **Используйте vendor** — для воспроизводимых сборок
9. **Документируйте зависимости** — объясняйте, почему используется зависимость
10. **Мониторьте уязвимости** — регулярно проверяйте зависимости на уязвимости
11. **Используйте приватные модули** — настраивайте **GOPRIVATE** для приватных репозиториев
12. **Используйте workspace** — для работы с несколькими модулями
13. **Автоматизируйте обновления** — используйте **Dependabot** или аналогичные инструменты
14. **Тестируйте обновления** — проверяйте работу после обновления зависимостей
15. **Версионируйте правильно** — следуйте правилам версионирования для **breaking changes**


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Go **Modules** предоставляют мощную систему управления зависимостями для Go проектов. Понимание создания модулей, управления зависимостями, версионирования, работы с **go.mod**, **vendoring**, приватными модулями, **workspace** и практических техник критично для эффективной работы с модулями в Go. Правильное использование модулей позволяет создавать воспроизводимые, безопасные, легко поддерживаемые и хорошо организованные проекты.

## Дополнительные ресурсы

- [Go Modules Documentation](https://go.dev/ref/mod)
- [Go Modules Wiki](https://github.com/golang/go/wiki/Modules)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
