---
title: "Вопросы на собеседовании: Go Modules"
description: "go.mod, go.sum, semantic versioning, vendoring, replace, workspace mode (go 1.18+), private modules, GOPROXY, dependency management в Go"
tags:
  - interview
  - programming-languages
  - go-modules-interview
aliases:
  - "Go modules interview"
  - "Go mod interview"
  - "Go dependency management"
  - "Go vendoring interview"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Go Modules`

С Go 1.11 (2018) появились **modules** — современная система управления зависимостями. Заменили `GOPATH` подход, дают воспроизводимые сборки, semantic versioning, и поддержку private/proxied модулей. С Go 1.18 — workspace mode для multi-module проектов.

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Go Modules Reference](https://go.dev/ref/mod)
- [Tutorial: Create a Go module](https://go.dev/doc/tutorial/create-module)
- [Go.mod file reference](https://go.dev/ref/mod#go-mod-file)
- [Workspaces tutorial](https://go.dev/doc/tutorial/workspaces)
- [Module Compatibility and Semantic Versioning](https://go.dev/blog/module-compatibility)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Go module?](#q1--что-такое-go-module)
- [Q2. (!) Зачем заменили GOPATH?](#q2--зачем-заменили-gopath)
- [Q3. (!) go.mod — что в нём?](#q3--gomod--что-в-нём)
- [Q4. (!) go.sum — для чего?](#q4--gosum--для-чего)

**Команды**
- [Q5. (!) go mod init, tidy, download?](#q5--go-mod-init-tidy-download)
- [Q6. (!) go get vs go install?](#q6--go-get-vs-go-install)
- [Q7. go list -m all — что показывает?](#q7-go-list--m-all--что-показывает)

**Versioning**
- [Q8. (!) Semantic versioning в Go?](#q8--semantic-versioning-в-go)
- [Q9. (!) Major versions (/v2, /v3) — особенности?](#q9--major-versions-v2-v3--особенности)
- [Q10. Pseudo-versions (v0.0.0-yyyymmddhhmmss-...)?](#q10-pseudo-versions-v000-yyyymmddhhmmss-)
- [Q11. Module Version Selection (MVS)?](#q11-module-version-selection-mvs)

**Replace и Vendor**
- [Q12. (!) replace директива — когда использовать?](#q12--replace-директива--когда-использовать)
- [Q13. exclude директива?](#q13-exclude-директива)
- [Q14. (!) Vendoring (go mod vendor)?](#q14--vendoring-go-mod-vendor)

**Workspaces**
- [Q15. (!) Что такое go workspace mode (1.18+)?](#q15--что-такое-go-workspace-mode-118)
- [Q16. go.work — что внутри?](#q16-gowork--что-внутри)

**Proxy и приватные модули**
- [Q17. (!) GOPROXY — что это и как работает?](#q17--goproxy--что-это-и-как-работает)
- [Q18. (!) GOPRIVATE для приватных модулей?](#q18--goprivate-для-приватных-модулей)
- [Q19. GOSUMDB — checksum database?](#q19-gosumdb--checksum-database)
- [Q20. Athens — private Go proxy?](#q20-athens--private-go-proxy)

**Best practices**
- [Q21. (!) Module path — соглашения?](#q21--module-path--соглашения)
- [Q22. Структура multi-module проекта?](#q22-структура-multi-module-проекта)
- [Q23. (!) Как обновить зависимости?](#q23--как-обновить-зависимости)
- [Q24. Минорная и патч-версии — auto update?](#q24-минорная-и-патч-версии--auto-update)

**Подводные камни**
- [Q25. (!) Diamond dependency problem?](#q25--diamond-dependency-problem)
- [Q26. Indirect зависимости — что это?](#q26-indirect-зависимости--что-это)
- [Q27. (!) Что делать, если нужна форка зависимости?](#q27--что-делать-если-нужна-форка-зависимости)

## Q1. (!) Что такое Go module?

**Module** — коллекция Go-пакетов с собственным versioning. Корень модуля — директория с файлом `go.mod`.

```
myapp/
  ├── go.mod         ← module path, dependencies
  ├── go.sum         ← checksums
  ├── main.go        ← package main
  ├── handler/
  │   ├── user.go    ← package handler
  │   └── order.go
  └── service/
      └── user.go    ← package service
```

Module path обычно совпадает с **import path**: `github.com/myorg/myapp`.

## Q2. (!) Зачем заменили GOPATH?

**До modules (GOPATH):**
- Все Go-проекты в `$GOPATH/src/`
- Один проект — одна версия каждой зависимости (на всю систему)
- Не было versioning — `go get` всегда брал master
- `vendor/` подход — копирование в каждый репо

**Проблемы:**
- Нет воспроизводимых сборок
- Конфликты версий между проектами
- Сложности с приватными модулями

**С modules:**
- Любая директория может быть проектом (не только в GOPATH)
- Each project — own dependency graph
- Точные версии в `go.mod`
- Hash-проверка через `go.sum`
- Pseudo-versions для unreleased коммитов

## Q3. (!) go.mod — что в нём?

```go
module github.com/myorg/myapp

go 1.21

require (
    github.com/gorilla/mux v1.8.0
    github.com/lib/pq v1.10.9
    github.com/stretchr/testify v1.8.4 // indirect
)

require (
    github.com/davecgh/go-spew v1.1.1 // indirect
    github.com/pmezard/go-difflib v1.0.0 // indirect
)

replace github.com/oldname/lib => github.com/newname/lib v1.2.3

exclude github.com/old/buggy v1.0.0
```

**Директивы:**
- `module` — путь модуля
- `go` — минимальная версия Go
- `require` — direct и indirect зависимости
- `replace` — переопределение
- `exclude` — исключение версии

`// indirect` — модуль не используется напрямую, а через зависимость.

## Q4. (!) go.sum — для чего?

`go.sum` хранит **криптографические хеши** всех зависимостей (включая transitive).

```
github.com/gorilla/mux v1.8.0 h1:i40aqfkR1h2SlN9hojwV5ZA91wcXFOvkdNIeFDP5koI=
github.com/gorilla/mux v1.8.0/go.mod h1:DVbg23sWSpFRCP0SfiEN6jmj59UnW/n46BH5rLB71So=
```

**Зачем:**
- Защита от подмены кода в репозиториях
- Воспроизводимость сборок
- Защита от supply chain attacks

`go mod download/build` проверяет хеши — если не совпадают, ошибка.

**Не редактируй вручную.** Обновляется автоматически.

## Q5. (!) go mod init, tidy, download?

```bash
# Инициализация — создаёт go.mod
go mod init github.com/myorg/myapp

# Tidy — добавляет используемые, удаляет неиспользуемые
go mod tidy

# Download — скачивает в кеш ($GOPATH/pkg/mod)
go mod download

# Vendor — копирует зависимости в vendor/
go mod vendor

# Verify — проверяет hash'и
go mod verify

# Why — почему этот модуль в зависимостях?
go mod why github.com/davecgh/go-spew
```

`go mod tidy` — самая частая команда. Запускается перед коммитом, чтобы `go.mod` отражал реальные импорты.

## Q6. (!) go get vs go install?

| Команда | Назначение |
|---------|------------|
| `go get` | Изменяет `go.mod` (добавляет/обновляет зависимость) |
| `go install` | Устанавливает binary в `$GOPATH/bin` |

```bash
# Добавить зависимость
go get github.com/gorilla/mux

# Обновить до последней
go get -u github.com/gorilla/mux

# Конкретная версия
go get github.com/gorilla/mux@v1.8.0

# Установить tool (локально, не в проект)
go install github.com/some/tool@latest
```

С Go 1.16+ — `go install` работает только для **named version** (нельзя `latest` без `@`).

## Q7. go list -m all — что показывает?

```bash
go list -m all       # все модули в зависимостях
go list -m -u all    # с информацией о доступных обновлениях
go list -m -versions github.com/gorilla/mux  # все версии
```

Output:
```
github.com/myorg/myapp
github.com/davecgh/go-spew v1.1.1
github.com/gorilla/mux v1.8.0
github.com/lib/pq v1.10.9
...
```

Полезно для аудита зависимостей.

## Q8. (!) Semantic versioning в Go?

Go строго требует **SemVer** (Semantic Versioning) для версий: `vMAJOR.MINOR.PATCH`.

| Версия | Изменение |
|--------|-----------|
| `v1.0.0` → `v1.0.1` | Bug fix, backwards compatible |
| `v1.0.0` → `v1.1.0` | New features, backwards compatible |
| `v1.0.0` → `v2.0.0` | **Breaking changes** |

**Префикс `v`** обязателен (`v1.2.3`, не `1.2.3`).

```go
require github.com/gorilla/mux v1.8.0
```

`v0.x.x` — pre-release, ничего не гарантировано.

## Q9. (!) Major versions (/v2, /v3) — особенности?

Для major versions ≥ 2, **module path** должен включать суффикс:

```go
// v1
import "github.com/gorilla/mux"

// v2 — путь меняется!
import "github.com/gorilla/mux/v2"

// v3
import "github.com/gorilla/mux/v3"
```

Это позволяет **импортировать несколько major versions одного модуля** в одной программе. Радикально для миграций (постепенное обновление).

В git — обычно отдельная ветка `v2/`, `v3/` или директория `/v2`, `/v3`.

## Q10. Pseudo-versions (v0.0.0-yyyymmddhhmmss-...)?

Когда у модуля нет git tag — Go генерирует **pseudo-version**:

```
v0.0.0-20230401120000-abcdef123456
```

Формат: `vMAJOR.MINOR.PATCH-yyyymmddhhmmss-shortcommithash`.

```bash
go get github.com/some/repo@main  # latest commit на main
# go.mod
require github.com/some/repo v0.0.0-20230401120000-abcdef123456
```

Используется для bleeding-edge версий или fork'ов без релизов.

## Q11. Module Version Selection (MVS)?

Алгоритм Go для выбора версий зависимостей: **Minimum Version Selection**.

**Правило:** для каждого модуля выбирается **минимальная** версия, удовлетворяющая всем требованиям.

```
A → B v1.2.0
A → C v1.0.0
C → B v1.5.0  // транзитивная зависимость

Результат: B v1.5.0 (минимум, удовлетворяющий обоим)
```

Это **детерминированно** и **воспроизводимо**. В отличие от npm/Maven, где выбирается **последняя** version, MVS — более консервативный.

**Преимущества MVS:**
- Меньше неожиданных breaking changes
- Воспроизводимые сборки
- Понятный алгоритм

## Q12. (!) replace директива — когда использовать?

```go
// Использовать локальную копию
replace github.com/myorg/lib => ../lib

// Использовать форк
replace github.com/old/lib => github.com/myfork/lib v1.0.1

// Конкретная версия
replace github.com/some/lib => github.com/some/lib v1.5.0

// Заблокировать версию
replace github.com/buggy/lib v1.0.0 => github.com/buggy/lib v1.0.1
```

**Use cases:**
1. **Локальная разработка** — тестировать local изменения в зависимости
2. **Hotfix** — пока upstream не выпустил
3. **Корпоративные форки** — в организации используется свой fork
4. **Замена**

`replace` действует **только** в текущем модуле. Не транзитивно (для downstream нужно их собственное `replace`).

## Q13. exclude директива?

```go
exclude github.com/buggy/lib v1.0.0
```

Запрещает использовать эту конкретную версию (например, известный bug или security issue). MVS выберет следующую совместимую.

Используется редко — обычно проще `replace` на нужную версию.

## Q14. (!) Vendoring (go mod vendor)?

```bash
go mod vendor  # создаёт vendor/ с копией всех зависимостей
```

```
myapp/
  ├── go.mod
  ├── go.sum
  ├── vendor/
  │   ├── modules.txt
  │   └── github.com/gorilla/mux/...
  └── ...
```

Если `vendor/` существует и `go.mod`/`go.sum` соответствуют — Go использует **vendored** код, не лезет в GOPROXY.

**Зачем:**
- **Воспроизводимость** даже если репо в proxy упал
- Air-gapped builds (без интернета)
- Audit dependencies в одном месте
- Корпоративные требования

**Минусы:**
- Большой репозиторий (~100 MB на проект)
- Нужно вручную обновлять (`go mod vendor`)

С modules + GOPROXY — vendoring используется реже. Но всё ещё актуально для security-sensitive проектов.

## Q15. (!) Что такое go workspace mode (1.18+)?

`Workspace` — режим для **многомодульной** разработки. Позволяет работать с несколькими модулями одновременно без `replace`.

```
project/
  ├── go.work           ← workspace
  ├── module-a/
  │   ├── go.mod
  │   └── ...
  ├── module-b/
  │   ├── go.mod
  │   └── ...
  └── module-c/
      ├── go.mod
      └── ...
```

```bash
cd project
go work init ./module-a ./module-b
go work use ./module-c
```

При сборке любого модуля — все workspaces видят локальные версии других модулей. Очень удобно для **monorepos**.

## Q16. go.work — что внутри?

```go
go 1.21

use (
    ./module-a
    ./module-b
    ./module-c
)

replace github.com/external/lib => github.com/myfork/lib v1.2.3
```

`go.work` **не коммитится** в git (обычно в `.gitignore`) — это локальная конфигурация разработчика.

В CI — собираем без `go.work` (или генерируем для multi-module CI).

## Q17. (!) GOPROXY — что это и как работает?

`GOPROXY` — URL прокси для скачивания модулей.

```bash
# Default
GOPROXY=https://proxy.golang.org,direct

# Возможные значения
GOPROXY=https://proxy.golang.org    # Google's official
GOPROXY=direct                       # напрямую с git
GOPROXY=off                          # запретить downloads
GOPROXY=https://my.corp.proxy        # корпоративный (Athens, Artifactory)
```

`,direct` означает fallback на git если proxy не имеет модуля.

**Преимущества proxy:**
- Cached versions — даже если репо удалён, версия доступна
- Защита от supply chain attacks (immutable artifacts)
- Ускорение downloads

`proxy.golang.org` — глобальный кеш Google. Бесплатный, public.

## Q18. (!) GOPRIVATE для приватных модулей?

```bash
GOPRIVATE=github.com/mycorp/*,gitlab.mycompany.com
```

Модули, попадающие под `GOPRIVATE`:
- НЕ идут через `GOPROXY` (запрос напрямую к git)
- НЕ проверяются в `GOSUMDB`

Для приватных репо нужно:

```bash
git config --global url."https://${TOKEN}@github.com/".insteadOf "https://github.com/"

# или SSH
git config --global url."git@github.com:".insteadOf "https://github.com/"
```

## Q19. GOSUMDB — checksum database?

`GOSUMDB=sum.golang.org` (default) — публичная база hash'ов всех публичных модулей. Защита от модификации репо.

```bash
GOSUMDB=off  # отключить (не рекомендуется!)
GOSUMDB=mycorp.com/sumdb  # корпоративная
```

Если `GOPRIVATE` указан — приватные модули **не** проверяются в GOSUMDB (потому что они не публичные).

## Q20. Athens — private Go proxy?

[Athens](https://github.com/gomods/athens) — open-source Go proxy для self-hosting.

**Use cases:**
- Корпоративный кеш (быстрее, чем proxy.golang.org из России/удалённых офисов)
- Air-gapped среды
- Аудит и контроль над dependencies
- Persistence (если репо удалят)

```bash
docker run -p 3000:3000 gomods/athens

# В клиенте
GOPROXY=http://athens.mycorp:3000
```

Альтернативы: **JFrog Artifactory**, **Sonatype Nexus** с Go репозиторием, **Buf** (для protobufs тоже).

## Q21. (!) Module path — соглашения?

```go
module github.com/myorg/myapp
module gitlab.mycorp.com/team/service
module example.com/internal/pkg
```

**Best practices:**
- Совпадает с git repo URL
- Lowercase
- Без `_` (используют `-`)

Это требование Go — `import` path должен указывать, откуда брать код.

## Q22. Структура multi-module проекта?

**Variant 1: один модуль, много пакетов** (наиболее частый):

```
myapp/
  go.mod
  cmd/
    server/main.go
    cli/main.go
  internal/
    handler/
    service/
    repo/
  pkg/
    publicapi/
```

**Variant 2: несколько модулей в одном репо** (редко):

```
mymonorepo/
  go.work
  service-a/
    go.mod
  service-b/
    go.mod
  shared-lib/
    go.mod
```

Multi-module — для очень больших monorepos. В большинстве случаев — **один модуль**.

`internal/` — особенный directory: пакеты внутри **доступны только** для родительского модуля.

## Q23. (!) Как обновить зависимости?

```bash
# Обновить ОДНУ зависимость до latest minor/patch
go get -u github.com/gorilla/mux

# До конкретной версии
go get github.com/gorilla/mux@v1.8.0

# Все зависимости до latest
go get -u ./...

# Только patch updates
go get -u=patch ./...

# Tidy после
go mod tidy
```

**Обновление major** (v1 → v2) **не автоматическое** — нужно поменять import path:

```go
// Было
import "github.com/gorilla/mux"

// Стало
import "github.com/gorilla/mux/v2"
```

## Q24. Минорная и патч-версии — auto update?

Go **не обновляет автоматически**. Версии в `go.mod` фиксированные.

Если хочешь автоматизировать:
- **Dependabot** (GitHub) — открывает PR при новых версиях
- **Renovate** — аналог
- **`go get -u`** в CI каждую неделю

Никогда не deploy без явного PR/review — обновление зависимости может ввести **breaking change** (даже если SemVer обещает обратное).

## Q25. (!) Diamond dependency problem?

Когда **разные пути** ведут к разным версиям одной зависимости:

```
A → B v1.0.0 → D v1.0.0
A → C v1.0.0 → D v2.0.0
```

В Java/npm — конфликт. В Go — благодаря **major version в import path**:

```go
import "example.com/d"        // v1
import "example.com/d/v2"      // v2
```

Можно **импортировать обе** одновременно. Они разные пакеты с точки зрения компилятора.

## Q26. Indirect зависимости — что это?

```go
require (
    github.com/gorilla/mux v1.8.0
    github.com/davecgh/go-spew v1.1.1 // indirect
)
```

`// indirect` — модуль не используется в твоём коде напрямую, а только через **другие зависимости**.

Появляется когда:
- Прямая зависимость требует через transitive
- Используется test-time зависимость

`go mod tidy` управляет indirect. Не трогай вручную.

## Q27. (!) Что делать, если нужна форка зависимости?

**Сценарий:** в `github.com/some/lib` есть bug, upstream не отвечает.

**Шаг 1:** Форкни на `github.com/myorg/some-lib`, поправь.

**Шаг 2:** В `go.mod`:
```go
replace github.com/some/lib => github.com/myorg/some-lib v1.0.1
```

**Шаг 3:** В fork сделай **новый tag** (или используй pseudo-version с конкретного коммита):
```bash
go get github.com/myorg/some-lib@abc1234
```

**Длинносрочно:** PR в upstream. Если не возьмут — поддерживай fork.

В корпоративной среде часто все зависимости **проксируются** через Artifactory/Athens с возможностью замены.

---

## See also

- [Go (базовый)](go-interview.md) — основы языка
- [Go Standard Library](go-stdlib-interview.md) — без внешних зависимостей
- [Go Testing](go-testing-interview.md) — testify, gomock как зависимости
- [Go Concurrency](go-concurrency-interview.md) — golang.org/x/sync/errgroup
- [Gradle и Maven](../../devops/gradle-maven-interview.md) — для сравнения dependency management
- [Git](../../devops/git-interview.md) — модули привязаны к git
- [Микросервисы](../../architecture/microservices-interview.md) — workspaces для monorepo
- [Application Security](../../security/application-security-interview.md) — supply chain (GOPROXY, GOSUMDB)

- [[go-concurrency-interview|Go Concurrency]]
- [[go-generics-interview|Go Generics]]
- [[go-interview|Go]]
- [[go-memory-gc-interview|Go Memory и GC]]
- [[go-stdlib-interview|Go Standard Library]]
- [[go-testing-interview|Go Testing]]
