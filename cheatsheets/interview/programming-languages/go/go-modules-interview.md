---
title: "Вопросы на собеседовании: Go Modules"
description: "go.mod, go.sum, semantic versioning, vendoring, replace, workspace mode (go 1.18+), private modules, GOPROXY, dependency management в Go"
tags:
  - interview
  - programming-languages
  - go-modules-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Go Modules"
  - "Go modules interview"
  - "Go mod interview"
prerequisites:
  - "[[go-modules]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Go Modules`

С Go 1.11 (2018) появились **modules** — современная система управления зависимостями. Заменили `GOPATH` подход, дают воспроизводимые сборки, semantic versioning, и поддержку private/proxied модулей. С Go 1.18 — workspace mode для multi-module проектов.

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


> [!mcq]
> - [x] Коллекция Go-пакетов с go.mod в корне, имеющая уникальный module path и собственное versioning | ✓ ПРИМЕНЯТЬ: каждый Go-проект начинается с go mod init 📋 ПРАВИЛО: Module = go.mod + module path + dependency graph 🔗 См. Q3
> - [ ] Отдельный .go файл с функцией package main | ❌ ПОСЛЕДСТВИЕ: путаница module/binary → неправильный go mod init → "cannot find package" при импорте
> - [ ] Директория с *.go файлами одного пакета | ❌ ПОСЛЕДСТВИЕ: модуль и пакет — разные уровни; один модуль содержит много пакетов → ошибка в ответе на интервью про dependency management
> - [ ] Файл go.sum с криптографическими хешами зависимостей | ❌ ПОСЛЕДСТВИЕ: редактирование go.sum вручную вместо go.mod → corrupted checksum database → build fails

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


> [!mcq]
> - [ ] GOPATH не поддерживал параллельную сборку нескольких пакетов | ❌ ПОСЛЕДСТВИЕ: неверная причина — GOPATH был медленным, но не из-за parallelism; истинная проблема (version conflicts) останется непонятой
> - [ ] GOPATH не работал на Windows из-за путей с обратным слешем | ❌ ПОСЛЕДСТВИЕ: поверхностный ответ → не поймёшь зачем go.sum и reproducible builds
> - [ ] GOPATH требовал сторонних инструментов (dep, glide) и не давал воспроизводимых сборок — у каждого dev была разная версия зависимостей | ✓ ПРИМЕНЯТЬ: объяснение ценности go.mod/go.sum 📋 ПРАВИЛО: GOPATH = no versioning + no reproducibility → modules исправили это 🔗 См. Q4
> - [ ] GOPATH не поддерживал приватные репозитории | ❌ ПОСЛЕДСТВИЕ: частичный ответ — приватные репо были проблемой, но главное — отсутствие versioning и воспроизводимости

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


> [!mcq]
> - [ ] Список всех .go файлов проекта с их build tags | ❌ ПОСЛЕДСТВИЕ: go.mod не содержит file list; попытка вручную перечислить файлы нарушит сборку
> - [ ] Хеши всех зависимостей для проверки целостности при сборке | ❌ ПОСЛЕДСТВИЕ: хеши хранит go.sum, не go.mod; редактирование go.mod для checksums — ошибка
> - [ ] Module path, версия Go, require/replace/exclude директивы с версиями зависимостей | ✓ ПРИМЕНЯТЬ: каждый раз при добавлении/удалении зависимости 📋 ПРАВИЛО: go.mod = manifest зависимостей; go.sum = integrity check 🔗 См. Q4
> - [ ] Конфигурация компилятора: GOOS, GOARCH, CGO_ENABLED | ❌ ПОСЛЕДСТВИЕ: build constraints идут в файлах через //go:build, а не в go.mod → неправильная кросс-компиляция

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


> [!mcq]
> - [x] Хранит SHA-256 хеши всех зависимостей (включая transitive) для защиты от подмены кода при сборке | ✓ ПРИМЕНЯТЬ: коммитить go.sum в git вместе с go.mod; не редактировать вручную 📋 ПРАВИЛО: go.sum = tamper protection; расхождение хеша → build error 🔗 См. Q3
> - [ ] Фиксирует точные версии как lock file в npm/yarn | ❌ ПОСЛЕДСТВИЕ: lock-file семантику выполняет go.mod (require секция); go.sum — об integrity, не о version pinning
> - [ ] Разрешает конфликты версий между транзитивными зависимостями | ❌ ПОСЛЕДСТВИЕ: конфликты версий разрешает MVS-алгоритм; go.sum только проверяет что скачанное соответствует ожидаемому
> - [ ] Описывает API каждой зависимости для static analysis | ❌ ПОСЛЕДСТВИЕ: go.sum содержит только хеши, не описания API → неправильные ожидания при audit зависимостей

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


> [!mcq]
> - [ ] go mod init создаёт go.sum; go mod tidy скачивает зависимости; go mod download обновляет go.mod | ❌ ПОСЛЕДСТВИЕ: перепутаны роли: init создаёт go.mod (не go.sum), tidy синхронизирует, download скачивает → команды будут выполняться неправильно
> - [ ] go mod init, tidy, download делают одно и то же — синхронизируют зависимости | ❌ ПОСЛЕДСТВИЕ: использование любой команды не по назначению → не создаётся go.mod или лишние downloads в CI
> - [ ] go mod tidy обновляет версии зависимостей до latest; go mod download проверяет хеши | ❌ ПОСЛЕДСТВИЕ: tidy НЕ обновляет до latest — только синхронизирует require с imports; обновление делает go get -u → неожиданные version bumps
> - [ ] go mod init создаёт go.mod с именем модуля; go mod tidy добавляет нужные и удаляет неиспользуемые require; go mod download кеширует зависимости локально | ✓ ПРИМЕНЯТЬ: tidy запускать перед каждым коммитом 📋 ПРАВИЛО: init=create, tidy=sync imports↔requires, download=cache 🔗 См. Q6

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


> [!mcq]
> - [x] go get изменяет go.mod (добавляет/обновляет зависимость проекта); go install компилирует и устанавливает binary в $GOPATH/bin | ✓ ПРИМЕНЯТЬ: go get для library deps; go install для CLI tools 📋 ПРАВИЛО: get=dependency management; install=binary install 🔗 См. Q5
> - [ ] go get скачивает в кеш без изменения go.mod; go install устанавливает в текущую директорию | ❌ ПОСЛЕДСТВИЕ: go get БЕЗ изменения go.mod — это go mod download; после go get нужен go mod tidy → неправильное управление зависимостями
> - [ ] go get и go install — синонимы с Go 1.16; оба устанавливают binary в PATH | ❌ ПОСЛЕДСТВИЕ: go get изменяет go.mod проекта, а install — нет; использование install вместо get сломает dependency graph
> - [ ] go install добавляет зависимость в go.mod и скачивает её; go get устанавливает tool глобально | ❌ ПОСЛЕДСТВИЕ: перепутаны роли — install используется для tools (@latest обязателен), get для project deps → installation tools через get загрязнит go.mod

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


> [!mcq]
> - [ ] Показывает все .go пакеты в текущем модуле с их import paths | ❌ ПОСЛЕДСТВИЕ: go list -m all показывает modules (не packages); для пакетов — go list ./...
> - [ ] Показывает только прямые зависимости из секции require в go.mod | ❌ ПОСЛЕДСТВИЕ: -m all включает transitive зависимости; для только прямых — читай go.mod без indirect
> - [ ] Показывает все версии конкретного модуля доступные на proxy | ❌ ПОСЛЕДСТВИЕ: все версии модуля — это go list -m -versions pkg; просто -m all показывает выбранные версии
> - [ ] Показывает полный граф зависимостей текущего модуля — все прямые и транзитивные зависимости с их версиями | ✓ ПРИМЕНЯТЬ: аудит зависимостей, поиск устаревших версий (-u флаг) 📋 ПРАВИЛО: -m all = все modules в build graph 🔗 См. Q11

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


> [!mcq]
> - [ ] Go использует MAJOR.MINOR.PATCH без префикса v; major bump разрешён без изменения import path | ❌ ПОСЛЕДСТВИЕ: префикс v обязателен (go.mod не примет 1.8.0); major bump без /v2 суффикса сломает импорты → compile error
> - [ ] Go требует только MAJOR.MINOR; patch версия опциональна | ❌ ПОСЛЕДСТВИЕ: go.sum хранит h1 хеш на конкретный patch; без patch в go.mod — неопределённость версии → нарушение воспроизводимости
> - [ ] vMAJOR.MINOR.PATCH обязателен; major version ≥ 2 требует изменить import path (добавить /v2) | ✓ ПРИМЕНЯТЬ: выпуск breaking change → новый module path /v2 📋 ПРАВИЛО: SemVer + v-prefix + /vN suffix для major ≥ 2 🔗 См. Q9
> - [ ] SemVer в Go необязателен; можно использовать git hash вместо версии | ❌ ПОСЛЕДСТВИЕ: git hash без тега → pseudo-version (v0.0.0-yyyymmdd-hash); Go не примет произвольный hash как версию в go.mod

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


> [!mcq]
> - [ ] Major version ≥ 2 требует создать отдельный git репозиторий с новым именем | ❌ ПОСЛЕДСТВИЕ: отдельный repo не нужен; достаточно /v2 в module path и git tag v2.x.x → создание лишних repo усложняет maintenance
> - [ ] При выпуске v2 все потребители автоматически обновляются через go get -u ./... | ❌ ПОСЛЕДСТВИЕ: major version — намеренно breaking; go get -u не перейдёт на v2 без явного изменения import path → пользователи застрянут на v1
> - [ ] Major versions в Go не поддерживаются; нужно делать новый module с другим именем | ❌ ПОСЛЕДСТВИЕ: это была практика до modules; теперь /v2 суффикс — официальный способ → создание mylib-v2 вместо mylib/v2 нарушает convention
> - [ ] Module path для v2+ должен включать /v2 суффикс; это позволяет одновременно использовать v1 и v2 в одном бинаре для постепенной миграции | ✓ ПРИМЕНЯТЬ: breaking API changes; постепенный migration 📋 ПРАВИЛО: major ≥ 2 → /vN в import path → разные пакеты компилятора 🔗 См. Q8

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


> [!mcq]
> - [x] Pseudo-version — автогенерируемая версия вида v0.0.0-yyyymmddhhmmss-commithash для коммитов без git tag | ✓ ПРИМЕНЯТЬ: при go get repo@commitHash или @branch; для unstable форков 📋 ПРАВИЛО: pseudo-version = timestamp + hash → нет тега → Go создаёт deterministic id 🔗 См. Q10
> - [ ] Pseudo-version — любая версия с pre-release label (v1.0.0-beta) | ❌ ПОСЛЕДСТВИЕ: pre-release и pseudo-version — разные вещи; pre-release — намеренный тег, pseudo — автогенерация → неправильный go get синтаксис
> - [ ] Pseudo-version можно написать вручную в go.mod для любого коммита | ❌ ПОСЛЕДСТВИЕ: формат псевдо-версии жёстко валидируется Go; неверный формат → go mod tidy выдаст ошибку "invalid pseudo-version"
> - [ ] Pseudo-version устарела с Go 1.18 и заменена workspace mode | ❌ ПОСЛЕДСТВИЕ: workspace и pseudo-version — разные features; workspace для local multi-module dev, pseudo-version для неtagged commits → продолжишь использовать replace когда нужен pseudo-version

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


> [!mcq]
> - [ ] MVS выбирает максимальную версию среди всех требований (latest wins) | ❌ ПОСЛЕДСТВИЕ: это поведение npm/Maven; в Go — минимальная версия; ожидание latest wins → неожиданные breaking changes при добавлении новой зависимости
> - [ ] MVS выбирает случайную совместимую версию для максимального разнообразия | ❌ ПОСЛЕДСТВИЕ: детерминизм — ключевое свойство MVS; случайность противоречит reproducible builds
> - [ ] MVS всегда выбирает версию из go.sum, игнорируя транзитивные зависимости | ❌ ПОСЛЕДСТВИЕ: MVS учитывает транзитивные зависимости; игнорирование transitive → version mismatch с зависимостями зависимостей
> - [ ] MVS выбирает минимальную версию, удовлетворяющую всем требованиям в dependency graph (включая транзитивные) | ✓ ПРИМЕНЯТЬ: при добавлении зависимости понимать что версия может подняться из-за transitive 📋 ПРАВИЛО: MVS = min(max(всех требований)) — не latest, а минимально достаточная 🔗 См. Q25

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


> [!mcq]
> - [x] replace переопределяет источник модуля: локальный путь для dev, форк с hotfix, конкретная версия; работает только в текущем модуле (не транзитивно) | ✓ ПРИМЕНЯТЬ: local dev с незафиненшенной зависимостью; корпоративный форк 📋 ПРАВИЛО: replace = локальный override; downstream должен добавить свой replace 🔗 См. Q13
> - [ ] replace обновляет go.sum с новыми хешами для указанной версии | ❌ ПОСЛЕДСТВИЕ: go.sum обновляется автоматически; replace — про source override, не про checksums → ручное редактирование go.sum сломает integrity
> - [ ] replace работает транзитивно — все зависимости автоматически увидят замену | ❌ ПОСЛЕДСТВИЕ: replace НЕ транзитивен; downstream модуль не видит replace из upstream → неожиданный production build без hotfix
> - [ ] replace нужна только для форков; для локальной разработки используется go mod vendor | ❌ ПОСЛЕДСТВИЕ: vendor и replace — разные вещи; vendor копирует все deps, replace лишь указывает альтернативный source → нельзя использовать vendor вместо replace для local dev

## Q13. exclude директива?

```go
exclude github.com/buggy/lib v1.0.0
```

Запрещает использовать эту конкретную версию (например, известный bug или security issue). MVS выберет следующую совместимую.

Используется редко — обычно проще `replace` на нужную версию.


> [!mcq]
> - [ ] exclude полностью удаляет модуль из dependency graph | ❌ ПОСЛЕДСТВИЕ: exclude запрещает только конкретную версию; другие версии модуля остаются доступны → непонимание приведёт к неожиданному использованию v1.0.1
> - [ ] exclude аналогичен require с указанием нижней границы версии | ❌ ПОСЛЕДСТВИЕ: require >= semantics нет в Go; exclude — точная блокировка одной версии; путаница приведёт к неправильному управлению уязвимыми версиями
> - [ ] exclude запрещает конкретную версию; MVS выбирает следующую совместимую, минуя заблокированную | ✓ ПРИМЕНЯТЬ: известный CVE в конкретной версии; известный regression 📋 ПРАВИЛО: exclude = pinpoint block; обычно проще replace на безопасную версию 🔗 См. Q11
> - [ ] exclude работает для всех downstream проектов автоматически | ❌ ПОСЛЕДСТВИЕ: как replace, exclude — не транзитивен; downstream должен добавить своё exclude → продолжит использовать уязвимую версию без явного исключения

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


> [!mcq]
> - [ ] go mod vendor обновляет go.mod с latest версиями всех зависимостей | ❌ ПОСЛЕДСТВИЕ: vendor только копирует текущие версии в vendor/; для update нужен go get -u → неожиданный freeze на устаревших версиях
> - [ ] vendor/ используется только для air-gapped сред; в обычной разработке он игнорируется | ❌ ПОСЛЕДСТВИЕ: если vendor/ существует, Go автоматически его использует вместо GOPROXY → неожиданное использование outdated кода после обновления go.mod без обновления vendor
> - [ ] go mod vendor копирует все зависимости в vendor/; при наличии vendor/ Go использует его без GOPROXY, что обеспечивает reproducible offline builds | ✓ ПРИМЕНЯТЬ: air-gapped CI; security audit зависимостей; корпоративные требования 📋 ПРАВИЛО: vendor = локальная копия; нужно обновлять вручную после go get 🔗 См. Q17
> - [ ] vendor/ автоматически обновляется при go build | ❌ ПОСЛЕДСТВИЕ: vendor не обновляется автоматически; после go get нужен явный go mod vendor → CI будет собирать старые версии

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


> [!mcq]
> - [ ] Workspace mode — аналог vendor для multi-module проектов; копирует все модули в workspace/ | ❌ ПОСЛЕДСТВИЕ: workspace не копирует код; он указывает Go использовать локальные пути вместо registry → неправильное использование go work init приведёт к path errors
> - [ ] go work позволяет обновлять все модули одной командой без перехода в каждый | ❌ ПОСЛЕДСТВИЕ: обновление зависимостей — это go get; workspace — про локальные cross-module changes без replace в каждом go.mod
> - [ ] Workspace mode (go.work) позволяет нескольким локальным модулям видеть друг друга без replace директив в go.mod | ✓ ПРИМЕНЯТЬ: monorepo с несколькими go.mod; разработка library + consumer одновременно 📋 ПРАВИЛО: go.work = dev-time override; не коммитится в git; CI собирает без него 🔗 См. Q16
> - [ ] go.work заменяет go.mod в multi-module репозиториях | ❌ ПОСЛЕДСТВИЕ: go.work не заменяет go.mod; оба нужны; go.work — дополнительный dev-tool; CI без go.work → production builds правильные

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


> [!mcq]
> - [x] go.work содержит use директивы с путями к локальным модулям и опциональные replace; обычно в .gitignore — это локальная конфигурация разработчика, не CI | ✓ ПРИМЕНЯТЬ: добавить go.work в .gitignore; CI строит без него 📋 ПРАВИЛО: go.work = dev overlay; .gitignore его, чтобы CI видел чистые deps 🔗 См. Q15
> - [ ] go.work коммитится в git как go.mod; CI использует его для multi-module builds | ❌ ПОСЛЕДСТВИЕ: go.work — локальная dev config с путями конкретного разработчика → коммит go.work сломает CI на других machines
> - [ ] go.work содержит секцию require как go.mod для определения версий workspace модулей | ❌ ПОСЛЕДСТВИЕ: версии зависимостей хранятся в go.mod каждого модуля, не в go.work; добавление require в go.work не работает
> - [ ] go.work заменяет все go.mod файлы в workspace — они становятся ненужными | ❌ ПОСЛЕДСТВИЕ: go.mod обязателен для каждого модуля; go.work лишь добавляет локальные override; удаление go.mod → "no go.mod found" error

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


> [!mcq]
> - [ ] GOPROXY — переменная для настройки HTTP proxy для всех сетевых запросов Go | ❌ ПОСЛЕДСТВИЕ: GOPROXY специфична только для module downloads; HTTP_PROXY/HTTPS_PROXY — для общего proxy; путаница приведёт к тому что GOPROXY=off не блокирует обычные HTTP запросы
> - [ ] GOPROXY используется только для приватных модулей; публичные скачиваются напрямую | ❌ ПОСЛЕДСТВИЕ: GOPRIVATE отключает proxy для приватных; GOPROXY по умолчанию используется для всех; прямой go→github без proxy → замедление и риск supply chain attack
> - [ ] GOPROXY указывает URL прокси для module downloads с fallback через запятую; `direct` означает обращение напрямую к git | ✓ ПРИМЕНЯТЬ: GOPROXY=off в production build для security; корпоративный Athens для cache 📋 ПРАВИЛО: GOPROXY=proxy,direct → сначала proxy, при 404 — git 🔗 См. Q18
> - [ ] GOPROXY=off блокирует только downloads новых модулей; уже скачанные доступны | ❌ ПОСЛЕДСТВИЕ: GOPROXY=off блокирует все модульные операции требующие network; если нет в кеше — build fails; нужен vendor или полный кеш

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


> [!mcq]
> - [x] GOPRIVATE — паттерн модулей, которые НЕ проксируются через GOPROXY и НЕ проверяются в GOSUMDB; Go идёт напрямую к git репозиторию | ✓ ПРИМЕНЯТЬ: internal корпоративные modules; нужна git auth через TOKEN или SSH 📋 ПРАВИЛО: GOPRIVATE = bypass proxy + bypass sumdb; аутентификация через git config 🔗 См. Q19
> - [ ] GOPRIVATE требует запустить приватный Go proxy (Athens); без него private modules недоступны | ❌ ПОСЛЕДСТВИЕ: приватный proxy удобен, но необязателен; GOPRIVATE без proxy работает через прямой git access; ожидание обязательного proxy усложняет setup
> - [ ] GOPRIVATE список модулей которые не могут быть импортированы другими проектами | ❌ ПОСЛЕДСТВИЕ: GOPRIVATE — про то как Go их скачивает, не про access control; видимость модуля контролируется git permissions, не GOPRIVATE
> - [ ] GOPRIVATE автоматически настраивает git credentials для приватных репозиториев | ❌ ПОСЛЕДСТВИЕ: GOPRIVATE не настраивает auth; нужен отдельный git config с token/SSH → private module download будет 401 без явной git конфигурации

## Q19. GOSUMDB — checksum database?

`GOSUMDB=sum.golang.org` (default) — публичная база hash'ов всех публичных модулей. Защита от модификации репо.

```bash
GOSUMDB=off  # отключить (не рекомендуется!)
GOSUMDB=mycorp.com/sumdb  # корпоративная
```

Если `GOPRIVATE` указан — приватные модули **не** проверяются в GOSUMDB (потому что они не публичные).


> [!mcq]
> - [ ] GOSUMDB — база известных уязвимостей в Go модулях (аналог CVE database) | ❌ ПОСЛЕДСТВИЕ: уязвимости проверяет govulncheck; GOSUMDB — про cryptographic integrity; отключение GOSUMDB не защитит от CVE
> - [ ] GOSUMDB = зеркало proxy.golang.org для ускорения downloads | ❌ ПОСЛЕДСТВИЕ: proxy и sumdb — разные сервисы с разными ролями; proxy кеширует модули, sumdb хранит хеши; путаница приведёт к неправильной конфигурации корпоративной среды
> - [ ] GOSUMDB хранит hash'и публичных модулей; приватные модули под GOPRIVATE не проверяются (они не публикуются в sumdb) | ✓ ПРИМЕНЯТЬ: GOSUMDB=off только если есть корпоративная sumdb; приватные модули добавить в GOPRIVATE 📋 ПРАВИЛО: GOSUMDB = tamper detection для публичных; GOPRIVATE bypass-ит sumdb 🔗 См. Q4
> - [ ] GOSUMDB обязателен для всех модулей включая приватные; отключить нельзя | ❌ ПОСЛЕДСТВИЕ: приватные модули объективно не могут быть в публичном sumdb; GOPRIVATE автоматически исключает их → жёсткое требование GOSUMDB для приватных сломает корпоративные builds

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


> [!mcq]
> - [x] Athens — open-source Go module proxy для self-hosting; используется как корпоративный кеш, air-gapped builds и контроль зависимостей | ✓ ПРИМЕНЯТЬ: корпоративная среда без прямого доступа к internet; air-gapped CI 📋 ПРАВИЛО: Athens = private GOPROXY; GOPROXY=http://athens → все downloads через него 🔗 См. Q17
> - [ ] Athens — утилита для автоматического обновления Go зависимостей (аналог Dependabot) | ❌ ПОСЛЕДСТВИЕ: Dependabot/Renovate для update notifications; Athens — proxy/cache; использование Athens ожидая автообновлений → зависимости никогда не обновятся
> - [ ] Athens нужен только для модулей v2+; v1 модули скачиваются через proxy.golang.org | ❌ ПОСЛЕДСТВИЕ: Athens проксирует все версии; ограничение "только v2+" не существует → неправильная конфигурация GOPROXY с частичной маршрутизацией
> - [ ] Athens заменяет go.sum; с Athens не нужна checksum verification | ❌ ПОСЛЕДСТВИЕ: Athens — прокси, не замена checksums; go.sum верификация происходит всегда независимо от proxy → отключение GOSUMDB при Athens — security риск

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


> [!mcq]
> - [ ] Module path — любая строка; Go не требует соответствия реальному URL | ❌ ПОСЛЕДСТВИЕ: Go не запрещает произвольный path, но GOPROXY не сможет найти модуль по фиктивному пути → CI fails при попытке go get другими проектами
> - [ ] Module path должен начинаться с доменного имени; github.com обязателен для публичных модулей | ❌ ПОСЛЕДСТВИЕ: любой домен допустим; example.com валиден для внутренних; требование github.com — неверное ограничение для корпоративных внутренних модулей
> - [ ] Module path должен совпадать с git repo URL (или содержать его как prefix), быть lowercase и использовать дефисы вместо underscore | ✓ ПРИМЕНЯТЬ: go mod init с реальным repo URL; проверять что path совпадает с тем откуда go get должен скачивать 📋 ПРАВИЛО: module path = import path = repo URL → deterministic discovery 🔗 См. Q17
> - [ ] Module path автоматически генерируется из имени директории при go mod init | ❌ ПОСЛЕДСТВИЕ: go mod init без аргумента создаёт go.mod без module path → compile error "no module declaration in go.mod"

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


> [!mcq]
> - [x] Обычно один модуль с many packages; multi-module (несколько go.mod) только для очень больших monorepo с go.work для local dev | ✓ ПРИМЕНЯТЬ: начинать с одного модуля; internal/ для пакетов не для внешнего использования 📋 ПРАВИЛО: один модуль = проще; multi-module = сложнее, только при явной необходимости 🔗 См. Q15
> - [ ] Каждый сервис обязан быть в отдельном go.mod даже в одном репозитории | ❌ ПОСЛЕДСТВИЕ: избыточная сложность; для большинства случаев один go.mod достаточен; multi-module без необходимости усложняет dependency management
> - [ ] internal/ директория блокирует компиляцию кода с флагом -race | ❌ ПОСЛЕДСТВИЕ: internal/ ограничивает импорт по module path, не компиляцию; код в internal/ компилируется и тестируется как обычно
> - [ ] go.work файл нужен только при наличии более 5 модулей в repo | ❌ ПОСЛЕДСТВИЕ: go.work нужен для любого multi-module repo при локальной разработке; порог в 5 — произвольный; нужен при первом cross-module изменении

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


> [!mcq]
> - [ ] go mod tidy обновляет все зависимости до latest version | ❌ ПОСЛЕДСТВИЕ: tidy только синхронизирует require с imports; не обновляет версии; для обновления нужен go get -u → зависимости застрянут на старых версиях
> - [ ] go mod download обновляет зависимости до latest; go mod tidy не нужен после | ❌ ПОСЛЕДСТВИЕ: download лишь кеширует; не изменяет go.mod; go mod tidy нужен после go get для очистки неиспользуемых → go.mod будет содержать лишние entries
> - [ ] go get -u ./... обновляет все зависимости до latest minor/patch; для конкретной версии используется @vX.Y.Z; go mod tidy очищает неиспользуемые | ✓ ПРИМЕНЯТЬ: регулярный update через -u ./... + review diff + go mod tidy 📋 ПРАВИЛО: update = go get -u; конкретная версия = @vX.Y.Z; major version = ручной import path change 🔗 См. Q6
> - [ ] Major version обновляется автоматически при go get -u без изменения import path | ❌ ПОСЛЕДСТВИЕ: major version — breaking change; автоматическое обновление невозможно; нужно вручную менять импорт на /v2 → silent build break если ожидать автоматику

## Q24. Минорная и патч-версии — auto update?

Go **не обновляет автоматически**. Версии в `go.mod` фиксированные.

Если хочешь автоматизировать:
- **Dependabot** (GitHub) — открывает PR при новых версиях
- **Renovate** — аналог
- **`go get -u`** в CI каждую неделю

Никогда не deploy без явного PR/review — обновление зависимости может ввести **breaking change** (даже если SemVer обещает обратное).


> [!mcq]
> - [x] Go не обновляет зависимости автоматически; версии зафиксированы в go.mod; для автоматизации нужен Dependabot/Renovate | ✓ ПРИМЕНЯТЬ: настроить Dependabot с auto-merge только для patch; major/minor требуют review 📋 ПРАВИЛО: go.mod = locked versions; автообновления через external tools, не Go toolchain 🔗 См. Q23
> - [ ] go build автоматически обновляет patch версии зависимостей при наличии новых | ❌ ПОСЛЕДСТВИЕ: go build никогда не изменяет go.mod; авто-update при build нарушил бы reproducibility → неожиданный broken build в CI
> - [ ] go mod tidy раз в день автоматически проверяет новые версии | ❌ ПОСЛЕДСТВИЕ: tidy — детерминированная операция без network для version checking; она не check updates → ложное ожидание что tidy обновит зависимости
> - [ ] MVS автоматически поднимает минорные версии при добавлении новых зависимостей | ❌ ПОСЛЕДСТВИЕ: MVS повышает версию только если новая зависимость требует выше; не обновляет безусловно → зависимости могут накапливать security vulnerabilities без явного update

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


> [!mcq]
> - [ ] Go решает diamond dependency как npm: берёт последнюю из требуемых версий | ❌ ПОСЛЕДСТВИЕ: Go использует MVS (минимальную); npm-поведение в Go не действует → неправильные ожидания о version selection при diamond
> - [ ] Diamond dependency неразрешим в Go; нужно вручную указать версию через replace | ❌ ПОСЛЕДСТВИЕ: replace — крайняя мера; в большинстве случаев MVS выбирает версию автоматически; manual replace для каждого diamond крайне трудоёмок
> - [ ] В Go diamond dependency решается через major version в import path: v1 и v2 — разные пакеты, можно использовать оба одновременно | ✓ ПРИМЕНЯТЬ: при migration v1→v2 можно использовать оба до полного перехода 📋 ПРАВИЛО: major versions = разные import paths = разные пакеты → no conflict 🔗 См. Q9
> - [ ] Go запрещает один проект использовать разные major versions одного модуля одновременно | ❌ ПОСЛЕДСТВИЕ: Go намеренно разрешает v1 и v2 одновременно для постепенной миграции; запрет противоречит design decision Go modules

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


> [!mcq]
> - [ ] // indirect означает что модуль deprecated и будет удалён в следующем релизе | ❌ ПОСЛЕДСТВИЕ: indirect — не признак устарелости; это просто transitive dependency; "удаление deprecated" через ручное редактирование go.mod сломает transitive requires
> - [ ] Indirect зависимости — зависимости только в тестах; в production они не включаются | ❌ ПОСЛЕДСТВИЕ: test-time deps — лишь один из случаев indirect; все transitive deps помечаются // indirect; исключение test deps из билда требует build tags, не indirect
> - [ ] // indirect означает что модуль нужен только транзитивно через другую зависимость; go mod tidy автоматически управляет этими записями | ✓ ПРИМЕНЯТЬ: не редактировать indirect вручную; go mod tidy расставит правильно 📋 ПРАВИЛО: indirect = нет прямого import в твоём коде; tidy добавляет/убирает автоматически 🔗 См. Q11
> - [ ] Indirect зависимости не включаются в go.sum и не проверяются checksums | ❌ ПОСЛЕДСТВИЕ: go.sum содержит хеши всех зависимостей включая indirect; исключение indirect из checksum — security hole

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


> [!mcq]
> - [ ] Создать новый модуль с другим именем (myorg/some-lib-patched); использовать прямой import | ❌ ПОСЛЕДСТВИЕ: форк с новым именем не совместим с indirect deps которые используют original path; весь dependency graph сломается
> - [x] replace директива в go.mod перенаправляет на форк; долгосрочно — PR в upstream или поддерживать форк; псевдо-версия при отсутствии тега | ✓ ПРИМЕНЯТЬ: критический hotfix пока upstream не отреагировал 📋 ПРАВИЛО: replace = path override; не транзитивен → downstream должен добавить своё replace 🔗 См. Q12
> - [ ] go mod vendor автоматически использует форк без изменения go.mod | ❌ ПОСЛЕДСТВИЕ: vendor копирует то что указано в go.mod; без replace форк не будет использован → баг останется в vendored коде
> - [ ] Подождать пока upstream примет PR; нет способа использовать fork немедленно | ❌ ПОСЛЕДСТВИЕ: replace именно для этого случая; ждать upstream — неприемлемо при critical bug; немедленное использование форка через replace — стандартная практика
