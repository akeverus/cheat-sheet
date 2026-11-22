---
title: "Go: базы данных"
description: "Полное руководство по работе с базами данных в Go: database/sql, sqlx, GORM, транзакции, миграции"
tags:
  - go
  - golang
  - database
  - sql
  - sqlx
  - gorm
type: "overview"
difficulty: "intermediate"
aliases:
  - "Go"
  - "базы данных"
  - "Go: базы данных"
  - "go database"
prerequisites:
  - "[[go-basics]]"
next: []
updated: "2026-04-20"
---

# Go: базы данных

## Полезные ссылки

- [Go database/sql Documentation](https://pkg.go.dev/database/sql)
- [sqlx Documentation](https://pkg.go.dev/github.com/jmoiron/sqlx)
- [GORM Documentation](https://gorm.io/docs/)

## Содержание

- [Введение в базы данных](#введение-в-базы-данных)
  - [Основные подходы](#основные-подходы)
- [database/sql](#databasesql)
  - [Подключение к базе данных](#подключение-к-базе-данных)
  - [Выполнение запросов](#выполнение-запросов)
  - [Запрос одной строки](#запрос-одной-строки)
  - [Подготовленные запросы](#подготовленные-запросы)
- [sqlx](#sqlx)
  - [Подключение](#подключение)
  - [Запросы с автоматическим маппингом](#запросы-с-автоматическим-маппингом)
  - [Именованные запросы](#именованные-запросы)
- [GORM](#gorm)
  - [Подключение](#подключение-1)
  - [Определение моделей](#определение-моделей)
  - [CRUD операции](#crud-операции)
  - [Запросы](#запросы)
- [Транзакции](#транзакции)
  - [database/sql транзакции](#databasesql-транзакции)
  - [GORM транзакции](#gorm-транзакции)
- [Миграции](#миграции)
  - [Использование migrate](#использование-migrate)
  - [GORM миграции](#gorm-миграции)
  - [Настройка пула соединений](#настройка-пула-соединений)
  - [Batch операции](#batch-операции)
  - [Batch операции с sqlx](#batch-операции-с-sqlx)
  - [Batch операции с GORM](#batch-операции-с-gorm)
  - [Сложные запросы с JOIN](#сложные-запросы-с-join)
  - [Сложные запросы с GORM](#сложные-запросы-с-gorm)
  - [Работа с NULL значениями](#работа-с-null-значениями)
  - [Работа с JSON в PostgreSQL](#работа-с-json-в-postgresql)
  - [Работа с массивами в PostgreSQL](#работа-с-массивами-в-postgresql)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Работа с большими результатами](#работа-с-большими-результатами)
  - [Pagination](#pagination)
  - [Repository Pattern](#repository-pattern)
  - [Unit of Work Pattern](#unit-of-work-pattern)
  - [Практические примеры: Connection Pooling](#практические-примеры-connection-pooling)
  - [Практические примеры: Health Check](#практические-примеры-health-check)
  - [Практические примеры: Query Builder](#практические-примеры-query-builder)
  - [Практические примеры: Database Migrations](#практические-примеры-database-migrations)
  - [Практические примеры: Repository паттерн с database/sql](#практические-примеры-repository-паттерн-с-databasesql)
  - [Практические примеры: Batch операции с транзакциями](#практические-примеры-batch-операции-с-транзакциями)
  - [Практические примеры: Запросы с динамическими условиями](#практические-примеры-запросы-с-динамическими-условиями)
  - [Практические примеры: Миграции с проверкой](#практические-примеры-миграции-с-проверкой)
  - [Практические примеры: Оптимизация запросов](#практические-примеры-оптимизация-запросов)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Оптимизация запросов через EXPLAIN](#практические-примеры-оптимизация-запросов-через-explain)
  - [Практические примеры: Кэширование результатов запросов](#практические-примеры-кэширование-результатов-запросов)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в базы данных

Go предоставляет несколько способов работы с базами данных: стандартный пакет `database/sql`, расширенный `sqlx` и **ORM** `GORM`. Понимание работы с базами данных критично для создания приложений, работающих с данными.

### Основные подходы

1. **database/sql** — стандартный пакет для работы с **SQL** базами данных
2. **sqlx** — расширение **database**/**sql** с дополнительными возможностями
3. **GORM** — **ORM** для работы с базами данных

## database/sql

Пакет `database/sql` предоставляет общий интерфейс для работы с **SQL** базами данных.

### Подключение к базе данных

```go
import (
    "context"
    "database/sql"
    "fmt"
    "time"

    _ "github.com/lib/pq"
)

func openPostgres(ctx context.Context, dsn string) (*sql.DB, error) {
    db, err := sql.Open("postgres", dsn)
    if err != nil {
        return nil, fmt.Errorf("open PostgreSQL pool: %w", err)
    }

    pingCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()
    if err := db.PingContext(pingCtx); err != nil {
        _ = db.Close()
        return nil, fmt.Errorf("ping PostgreSQL: %w", err)
    }

    return db, nil
}
```

Пустой импорт регистрирует драйвер `lib/pq` под именем `postgres`. DSN передаёт вызывающий код из конфигурации. Для удалённого PostgreSQL с TLS параметр `sslmode=verify-full` проверяет доверие к сертификату и совпадение имени сервера. Настройте доверенный CA для сертификата сервера.

`sql.Open` возвращает дескриптор пула и может завершиться до установки соединения. `PingContext` проверяет доступность БД в момент запуска. Его таймаут относится к проверке. У последующих запросов свои контексты. Приложение переиспользует успешно открытый пул для запросов и закрывает его при завершении. Если проверка не прошла, функция закрывает пул сама.

[Go — sql.Open](https://pkg.go.dev/database/sql#Open) · [Go — PingContext](https://pkg.go.dev/database/sql#DB.PingContext) · [lib/pq — параметры подключения](https://pkg.go.dev/github.com/lib/pq#hdr-Connection_String_Parameters)

### Выполнение запросов

```go
import "database/sql"

// Выполнение запроса без результата
_, err := db.Exec("CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT)")

// Выполнение запроса с параметрами
_, err := db.Exec("INSERT INTO users (name) VALUES ($1)", "Alice")

// Выполнение запроса с результатом
func printUsers(ctx context.Context, db *sql.DB) error {
    rows, err := db.QueryContext(ctx, "SELECT id, name FROM users")
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var id int
        var name string
        if err := rows.Scan(&id, &name); err != nil {
            return err
        }
        fmt.Printf("ID: %d, Name: %s\n", id, name)
    }
    return rows.Err()
}
```

`Scan` сообщает об ошибке чтения текущей строки, например при несовместимом типе столбца. `Next` возвращает `false` и при исчерпании строк, и при ошибке итерации. Поэтому после цикла проверяют `Rows.Err()`: если соединение оборвалось после нескольких строк, вызывающий код получит ошибку, хотя часть вывода уже появилась. Для запроса с одним набором результатов `database/sql` закрывает `Rows` при исчерпании строк. Отложенный `Close` закрывает их и при раннем выходе. [Документация Rows](https://pkg.go.dev/database/sql#Rows).

### Запрос одной строки

```go
var user User
err := db.QueryRow("SELECT id, name FROM users WHERE id = $1", 1).
    Scan(&user.ID, &user.Name)
switch {
case err == sql.ErrNoRows:
    fmt.Println("Пользователь отсутствует")
case err != nil:
    log.Print(err)
default:
    fmt.Println(user.Name)
}
```

`QueryRow` откладывает ошибки до `Scan`. При пустом результате `Scan` возвращает `sql.ErrNoRows`, а при нескольких строках читает первую и отбрасывает остальные. Здесь уникальность поиска обеспечивает первичный ключ `users.id`. [Документация QueryRow](https://pkg.go.dev/database/sql#DB.QueryRow).

### Подготовленные запросы

Подготовьте часто используемое выражение один раз и передайте `Stmt` коду, который выполняет запросы. В примере PostgreSQL контекст запуска ограничивает подготовку, а каждый вызов `userNameByID` получает контекст своего запроса.

```go
func prepareUserNameQuery(ctx context.Context, db *sql.DB) (*sql.Stmt, error) {
    return db.PrepareContext(ctx, "SELECT name FROM users WHERE id = $1")
}

func userNameByID(ctx context.Context, stmt *sql.Stmt, id int64) (string, error) {
    var name string
    if err := stmt.QueryRowContext(ctx, id).Scan(&name); err != nil {
        return "", err
    }
    return name, nil
}
```

Код, который владеет `Stmt`, вызывает `Close` после завершения работы с ним. Подготовленный через `DB.PrepareContext` объект можно использовать одновременно из нескольких горутин. Если для выполнения понадобится другое физическое соединение, `database/sql` подготовит выражение и на нём. Контекст подготовки не задаёт таймаут для последующих вызовов `QueryRowContext`. [DB.PrepareContext](https://pkg.go.dev/database/sql#DB.PrepareContext) · [Документация Stmt](https://pkg.go.dev/database/sql#Stmt)

В транзакции подготовьте выражение через `tx.PrepareContext` или получите привязанную к транзакции версию через `tx.StmtContext(ctx, stmt)`. Прямой вызов исходного `stmt`, подготовленного на `db`, выполнится через пул вне транзакции. Выражение, подготовленное на `Tx` или `Conn`, привязано к одному соединению и перестаёт быть пригодным после завершения `Tx` или закрытия `Conn`. [Tx.StmtContext](https://pkg.go.dev/database/sql#Tx.StmtContext)

## sqlx

Пакет `sqlx` расширяет `database/sql` сопоставлением колонок с полями структур и именованными параметрами. Правила работы с пулом и транзакциями сохраняются.

### Подключение

```go
func openSQLX(ctx context.Context, dsn string) (*sqlx.DB, error) {
    return sqlx.ConnectContext(ctx, "postgres", dsn)
}
```

Пример предполагает импорт `github.com/jmoiron/sqlx` и регистрацию драйвера через пустой импорт `github.com/lib/pq`. `ConnectContext` открывает дескриптор БД и проверяет подключение. Вызывающий код передаёт DSN из конфигурации и закрывает успешно открытый пул при остановке приложения.

### Запросы с автоматическим маппингом

```go
type User struct {
    ID   int    `db:"id"`
    Name string `db:"name"`
}

func findUser(ctx context.Context, db *sqlx.DB, id int) (User, error) {
    var user User
    err := db.GetContext(ctx, &user, "SELECT id, name FROM users WHERE id = $1", id)
    return user, err
}

func listUsers(ctx context.Context, db *sqlx.DB, limit int) ([]User, error) {
    var users []User
    err := db.SelectContext(ctx, &users,
        "SELECT id, name FROM users ORDER BY id LIMIT $1", limit)
    return users, err
}
```

По умолчанию sqlx возвращает ошибку, если колонке результата не соответствует поле структуры. Явный список колонок сохраняет контракт при добавлении полей в таблицу. `GetContext` возвращает `sql.ErrNoRows`, когда строка отсутствует. `SelectContext` собирает весь результат в слайс, поэтому в примере вызывающий код передаёт ограниченный размер выборки.

### Именованные запросы

```go
func findUserByName(ctx context.Context, db *sqlx.DB, id int, name string) (User, error) {
    const query = "SELECT id, name FROM users WHERE id = :id AND name = :name"
    bound, args, err := db.BindNamed(query, map[string]any{"id": id, "name": name})
    if err != nil {
        return User{}, err
    }

    var user User
    err = db.GetContext(ctx, &user, bound, args...)
    return user, err
}
```

`BindNamed` подбирает плейсхолдеры для драйвера и возвращает значения отдельными аргументами. Для PostgreSQL это плейсхолдеры вида `$1`. Ошибку сопоставления, например при отсутствии ключа в map, обрабатывают до выполнения SQL.

[Документация sqlx](https://pkg.go.dev/github.com/jmoiron/sqlx) · [Руководство sqlx](https://jmoiron.github.io/sqlx/)

## GORM

**GORM** сопоставляет структуры Go с таблицами и строит SQL для чтения и изменения данных. Ниже используется традиционный API GORM v2, где ошибки операций доступны через поле `.Error`.

### Подключение

Драйвер PostgreSQL для GORM умеет создать собственный пул или использовать уже открытый `*sql.DB`. Ниже используем пул из примера `openPostgres`, который уже проверил соединение с таймаутом.

```go
import (
    "context"
    "database/sql"

    "gorm.io/driver/postgres"
    "gorm.io/gorm"
)

func openGORM(ctx context.Context, dsn string) (*gorm.DB, *sql.DB, error) {
    pool, err := openPostgres(ctx, dsn)
    if err != nil {
        return nil, nil, err
    }

    db, err := gorm.Open(postgres.New(postgres.Config{Conn: pool}), &gorm.Config{
        DisableAutomaticPing: true,
    })
    if err != nil {
        _ = pool.Close()
        return nil, nil, err
    }

    return db, pool, nil
}
```

`DisableAutomaticPing` убирает повторную проверку внутри `gorm.Open`. Здесь драйвер пула — `lib/pq`, зарегистрированный в `openPostgres`. При `postgres.Open(dsn)` без переданного пула драйвер GORM по умолчанию использует pgx. После успешного запуска приложение настраивает и закрывает общий `pool`. В операции GORM передавайте контекст запроса через `WithContext(ctx)`.

[Подключение GORM к PostgreSQL](https://gorm.io/docs/connecting_to_the_database.html#PostgreSQL) · [Автоматическая проверка соединения](https://gorm.io/docs/gorm_config.html#DisableAutomaticPing) · [Контекст GORM](https://gorm.io/docs/context.html)

### Определение моделей

```go
type User struct {
    ID    uint   `gorm:"primaryKey"`
    Name  string
    Email string `gorm:"uniqueIndex"`
    Age   int
}

// Для отдельного шага настройки схемы в локальном окружении.
if err := db.AutoMigrate(&User{}); err != nil {
    return err
}
```

### CRUD операции

```go
func userCRUD(ctx context.Context, db *gorm.DB) error {
    db = db.WithContext(ctx)

    user := User{Name: "Alice", Email: "alice@example.com", Age: 24}
    if err := db.Create(&user).Error; err != nil {
        return err
    }

    var loaded User
    if err := db.First(&loaded, user.ID).Error; err != nil {
        return err
    }

    if err := db.Model(&loaded).Updates(map[string]any{
        "name": "Bob",
        "age":  0,
    }).Error; err != nil {
        return err
    }

    return db.Delete(&loaded).Error
}
```

Пример показывает отдельные операции. Чтобы объединить их в одну транзакцию, используйте `db.Transaction` и выполняйте операции через переданный `tx`.

При отсутствии строки `First` возвращает ошибку, которую можно распознать через `errors.Is(err, gorm.ErrRecordNotFound)`. У `Find` пустая выборка успешна. Проверяйте `.Error`, затем длину результата или `.RowsAffected`. [Чтение и ошибки GORM](https://gorm.io/docs/query.html)

`Updates` со структурой по умолчанию пропускает нулевые значения. В примере карта позволяет записать `age = 0`. Традиционный `Save` обновляет все поля и при нуле затронутых строк может перейти к вставке. Для операции, которая должна только обновлять существующую запись, используйте `Update` или `Updates` с нужным условием. [Обновление в GORM](https://gorm.io/docs/update.html)

### Запросы

```go
var users []User
result := db.WithContext(ctx).
    Where("age > ?", 18).
    Select("id", "name", "email").
    Order("name DESC, id ASC").
    Limit(10).
    Offset(20).
    Find(&users)
if result.Error != nil {
    return result.Error
}
```

`id` задаёт порядок между пользователями с одинаковым именем. Поля вне `Select` отсутствуют в результате запроса. Для фильтра по нулевому значению используйте явное условие вроде `Where("age = ?", 0)` или карту. Условие `Where(&User{Age: 0})` пропустит поле `Age`. [Условия и выбор полей GORM](https://gorm.io/docs/query.html)

## Транзакции

Транзакции обеспечивают атомарность операций.

### database/sql транзакции

В PostgreSQL обе вставки выполняются через один `sql.Tx`. Функция возвращает успех после успешного `Commit`, чтобы вызывающий код мог обработать ошибку фиксации.

```go
func insertUsers(ctx context.Context, db *sql.DB) error {
    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return err
    }
    defer tx.Rollback()

    const query = "INSERT INTO users (name) VALUES ($1)"
    if _, err := tx.ExecContext(ctx, query, "Alice"); err != nil {
        return err
    }
    if _, err := tx.ExecContext(ctx, query, "Bob"); err != nil {
        return err
    }
    return tx.Commit()
}
```

Если вторая вставка вернёт ошибку, отложенный `Rollback` отменит первую. После успешного `Commit` отложенный вызов уже не меняет результат транзакции. Контекст `BeginTx` действует до завершения транзакции, а при его отмене `database/sql` откатывает её. [Транзакции в Go](https://go.dev/doc/database/execute-transactions) · [Контракт BeginTx](https://pkg.go.dev/database/sql#DB.BeginTx).

**Что делать, если `Commit` вернул ошибку?** Различайте отказ БД и потерю подтверждения. Например, если соединение оборвалось после отправки `COMMIT`, сервер мог зафиксировать изменения до того, как клиент получил ответ. Отложенный `Rollback` не отменит уже зафиксированную транзакцию.

Для команды создания заказа запишите уникальный идентификатор запроса вместе с заказом в той же транзакции. При повторе используйте прежний идентификатор. Если первая попытка уже зафиксировалась, ограничение уникальности предотвратит второй заказ, а приложение сможет вернуть результат первой попытки. Одиночное чтение с отстающей реплики не подтверждает, что заказа нет.

[Go — контракт `Commit`](https://go.dev/doc/database/execute-transactions) · [PostgreSQL — коды ошибок транзакций и соединений](https://www.postgresql.org/docs/current/errcodes-appendix.html)

### GORM транзакции

```go
func createUsers(ctx context.Context, db *gorm.DB, first, second User) error {
    return db.WithContext(ctx).Transaction(func(tx *gorm.DB) error {
        if err := tx.Create(&first).Error; err != nil {
            return err
        }
        return tx.Create(&second).Error
    })
}
```

Если вторая вставка нарушит уникальность email, ошибка выйдет из callback и GORM откатит обе вставки. При `nil` из callback GORM попытается зафиксировать транзакцию. Возвращаемое значение `Transaction` нужно проверить, поскольку ошибка возможна и при фиксации. Все операции, которые входят в этот блок, выполняйте через `tx`, включая вызовы из вспомогательных функций. Запрос через исходный `db` выполняется вне этой транзакции.

В PostgreSQL вложенный вызов `tx.Transaction(...)` при стандартной конфигурации GORM использует savepoint. Ошибка вложенного блока откатывает его изменения до этой точки. Если внешняя функция тоже вернёт ошибку, откатится вся транзакция. Успех вложенного блока становится видимым другим транзакциям по правилам изоляции после внешнего commit. При `DisableNestedTransaction: true` GORM не создаёт savepoint автоматически, поэтому рассчитывать на частичный откат нельзя.

[Транзакции GORM](https://gorm.io/docs/transactions.html) · [Настройки сессии GORM](https://gorm.io/docs/session.html#DisableNestedTransaction)

## Миграции

Миграции позволяют управлять схемой базы данных.

### Использование migrate

```go
import (
    "errors"

    "github.com/golang-migrate/migrate/v4"
    _ "github.com/golang-migrate/migrate/v4/database/postgres"
    _ "github.com/golang-migrate/migrate/v4/source/file"
)

func migratePostgres(databaseURL string) (err error) {
    m, err := migrate.New("file://migrations", databaseURL)
    if err != nil {
        return err
    }
    defer func() {
        sourceErr, databaseErr := m.Close()
        err = errors.Join(err, sourceErr, databaseErr)
    }()

    if err := m.Up(); !errors.Is(err, migrate.ErrNoChange) {
        return err
    }
    return nil
}
```

Передайте URL подключения из конфигурации. Импорты с `_` регистрируют драйверы PostgreSQL и файлового источника. При повторном запуске `ErrNoChange` означает, что новых миграций нет. Если инструмент сообщает о состоянии `dirty`, сначала сопоставьте версию и фактическую схему с SQL проблемной миграции. `Force` меняет записанную версию и снимает признак `dirty`. Исправление схемы выполняют отдельно. Для `errors.Join` нужен Go 1.20+. [API golang-migrate](https://pkg.go.dev/github.com/golang-migrate/migrate/v4) · [FAQ о состоянии dirty](https://github.com/golang-migrate/migrate/blob/master/FAQ.md)

### GORM миграции

```go
if err := db.AutoMigrate(&User{}, &Post{}); err != nil {
    return fmt.Errorf("migrate schema: %w", err)
}
```

`AutoMigrate` приводит поддерживаемые элементы схемы в соответствие с моделями и сохраняет лишние столбцы. Для удаления столбца нужна явная операция, например `Migrator().DropColumn` в отдельной миграции. Если порядок изменений важен для совместимости версий приложения, храните шаги в версионируемых миграциях. [Миграции GORM](https://gorm.io/docs/migration.html)

### Настройка пула соединений

```go
func configureConnectionPool(db *sql.DB) {
    // Максимальное количество открытых соединений
    db.SetMaxOpenConns(25)

    // Максимальное количество неактивных соединений
    db.SetMaxIdleConns(5)

    // Предельный возраст соединения для повторного использования
    db.SetConnMaxLifetime(5 * time.Minute)

    // Максимальное время простоя соединения
    db.SetConnMaxIdleTime(1 * time.Minute)
}
```

Числа в примере условные. `SetMaxOpenConns` ограничивает общее число открытых соединений одного `sql.DB`, включая занятые и свободные. `SetMaxIdleConns` ограничивает число свободных соединений, которые пул сохраняет. При исчерпании лимита новая операция ждёт освобождения соединения или отмены своего контекста. `SetConnMaxLifetime` и `SetConnMaxIdleTime` управляют повторным использованием соединений. Дедлайн запроса задают отдельно, например через контекст `QueryContext`. [Управление соединениями](https://go.dev/doc/database/manage-connections) · [Контракт SetConnMaxLifetime](https://pkg.go.dev/database/sql#DB.SetConnMaxLifetime).

**Пример с одним соединением.** При `SetMaxOpenConns(1)` открытая транзакция занимает единственное соединение. Если её код вызывает `db.QueryContext` и ждёт результат до завершения транзакции, запрос ждёт то же соединение. Дедлайн прервёт ожидание. Если запрос относится к этой транзакции, выполняйте его через `tx.QueryContext`. Чтобы обнаружить регулярное ожидание в рабочем приложении, сравнивайте прирост `DB.Stats().WaitCount` и `WaitDuration` за один и тот же интервал. [Статистика пула](https://pkg.go.dev/database/sql#DBStats).

**Бюджет соединений при раскатке.** Допустим, приложение напрямую подключается к одному серверу PostgreSQL. Из 150 подключений 10 зарезервированы сервером для привилегированных ролей, а ещё 20 выделены другим клиентам. Во время раскатки одновременно могут работать до 12 экземпляров сервиса, у каждого один `sql.DB`. Тогда потолок для одного пула — `(150 - 10 - 20) / 12 = 10` соединений. При нескольких пулах к тому же серверу учитывайте сумму их лимитов. Лимит 25 из примера ниже в таком сценарии допускал бы до 300 соединений приложения, и сервер мог бы отклонять новые подключения под нагрузкой.

Полученные 10 соединений — верхняя граница для этих исходных условий. Рабочее значение выбирайте по времени ожидания в пуле и нагрузке на БД. Например, при росте `WaitDuration` проверьте, сколько времени операции держат соединения и есть ли у сервера запас. Увеличение пула при уже перегруженной БД может усилить конкуренцию за её ресурсы. [Параметры соединений PostgreSQL](https://www.postgresql.org/docs/19/runtime-config-connection.html#RUNTIME-CONFIG-CONNECTION-SETTINGS) · [Управление соединениями в Go](https://go.dev/doc/database/manage-connections)

### Batch операции

```go
// Batch insert с database/sql
func batchInsert(ctx context.Context, db *sql.DB, users []User) error {
    if len(users) == 0 {
        return nil
    }

    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return err
    }
    defer tx.Rollback()

    stmt, err := tx.PrepareContext(ctx, "INSERT INTO users (name, email) VALUES ($1, $2)")
    if err != nil {
        return err
    }
    defer stmt.Close()

    for _, user := range users {
        _, err := stmt.ExecContext(ctx, user.Name, user.Email)
        if err != nil {
            return err
        }
    }

    return tx.Commit()
}
```

Один подготовленный оператор выполняется отдельно для каждого пользователя в общей транзакции. Если вторая вставка нарушит ограничение уникальности, отложенный `Rollback` отменит и первую. Ошибка `Commit` возвращается вызывающему коду.

[Go — выполнение транзакций](https://go.dev/doc/database/execute-transactions)

### Batch операции с sqlx

```go
func batchInsertWithSqlx(ctx context.Context, db *sqlx.DB, users []User) error {
    if len(users) == 0 {
        return nil
    }

    query := `INSERT INTO users (name, email) VALUES (:name, :email)`
    _, err := db.NamedExecContext(ctx, query, users)
    return err
}
```

В sqlx 1.4.0 для этого шаблона библиотека разворачивает срез в один `INSERT` с группой `VALUES` на пользователя. Проверка пустого среза предотвращает ошибку привязки параметров. Для большого списка учитывайте предел числа параметров и размер запроса.

[sqlx 1.4.0 — привязка параметров среза](https://github.com/jmoiron/sqlx/blob/v1.4.0/named.go)

### Batch операции с GORM

```go
func batchInsertWithGORM(ctx context.Context, db *gorm.DB, users []User) error {
    if len(users) == 0 {
        return nil
    }

    return db.WithContext(ctx).CreateInBatches(&users, 100).Error
}
```

Размер 100 задаёт число записей в порции. Если импорт разбит на несколько самостоятельных вызовов, для атомарности всего импорта объедините их одной транзакцией и выполняйте каждый вызов через её объект.

[GORM — пакетная вставка](https://gorm.io/docs/create.html#Batch-Insert)

### Сложные запросы с JOIN

Пример для PostgreSQL. Сначала выбираем страницу из 50 пользователей с положительными ID, затем присоединяем их публикации. Для первой страницы передайте `afterID = 0`, для следующей — ID последнего пользователя.

```go
type JoinedPost struct {
    ID    int64
    Title sql.NullString
}

type UserWithPosts struct {
    ID    int64
    Name  string
    Posts []JoinedPost
}

func getUsersWithPosts(ctx context.Context, db *sql.DB, afterID int64) ([]UserWithPosts, error) {
    const query = `
        SELECT u.id, u.name, p.id, p.title
        FROM (
            SELECT id, name
            FROM users
            WHERE id > $1
            ORDER BY id
            LIMIT 50
        ) u
        LEFT JOIN posts p ON p.user_id = u.id
        ORDER BY u.id, p.id
    `

    rows, err := db.QueryContext(ctx, query, afterID)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var users []UserWithPosts
    for rows.Next() {
        var user UserWithPosts
        var postID sql.NullInt64
        var title sql.NullString
        if err := rows.Scan(&user.ID, &user.Name, &postID, &title); err != nil {
            return nil, err
        }

        if len(users) == 0 || users[len(users)-1].ID != user.ID {
            users = append(users, user)
        }
        if postID.Valid {
            current := &users[len(users)-1]
            current.Posts = append(current.Posts, JoinedPost{ID: postID.Int64, Title: title})
        }
    }
    if err := rows.Err(); err != nil {
        return nil, err
    }
    return users, nil
}
```

`ORDER BY u.id, p.id` собирает строки одного пользователя рядом. Если публикаций нет, `LEFT JOIN` всё равно возвращает пользователя, а поля публикации приходят как `NULL`. Проверяем первичный ключ публикации, чтобы отличить отсутствие публикации от публикации с пустым или `NULL`-заголовком. Пример предполагает, что `users.name` задано. Если чтение прерывается после получения нескольких строк, `rows.Err()` позволяет вернуть ошибку вместо неполной страницы.

Лимит в подзапросе действует на пользователей. Объём публикаций на странице может быть существенно больше. Если запрос должен ограничивать и его, задайте отдельную пагинацию публикаций.

[Документация PostgreSQL по JOIN](https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-JOIN) · [Документация Go по Rows.Err](https://pkg.go.dev/database/sql#Rows.Err)

### Сложные запросы с GORM

```go
type User struct {
    ID    uint
    Name  string
    Posts []Post `gorm:"foreignKey:UserID"`
}

type Post struct {
    ID     uint
    Title  string
    UserID uint
}

func getUsersWithPostsGORM(ctx context.Context, db *gorm.DB, afterID uint) ([]User, error) {
    var users []User
    err := db.WithContext(ctx).
        Where("id > ?", afterID).
        Order("id").
        Limit(50).
        Preload("Posts").
        Find(&users).Error
    if err != nil {
        return nil, err
    }
    return users, nil
}
```

Передайте `afterID = 0` для первой страницы с положительными ID. Для следующей страницы передайте последний ID из полученного списка. Для непустой страницы GORM загружает пользователей одним запросом и их публикации отдельным запросом с условием `user_id IN (...)`. Это заменяет отдельный запрос публикаций для каждого пользователя. Ограничение в 50 строк относится только к пользователям. Если у одного пользователя тысячи публикаций, для них нужна отдельная пагинация.

Условие внутри `Preload("Posts", ...)` фильтрует публикации. Чтобы выбрать только пользователей, у которых есть подходящая публикация, добавьте условие в запрос пользователей, например через `EXISTS`. `Join Preload` в GORM рассчитан на связи «один к одному», такие как `has one` и `belongs to`. При ручном SQL JOIN для связи «один ко многим» лимит на объединённый результат ограничивает пары «пользователь — публикация». Чтобы сохранить размер страницы пользователей, сначала выберите страницу родительских строк.

[Загрузка связей GORM](https://gorm.io/docs/preload.html) · [Связь has many](https://gorm.io/docs/has_many.html)

### Работа с NULL значениями

В примере PostgreSQL столбцы `id` и `name` имеют ограничение `NOT NULL`, а `email` и `age` допускают `NULL`.

```go
type UserContact struct {
    ID    int64
    Name  string
    Email sql.NullString
    Age   sql.NullInt64
}

func findUserContact(ctx context.Context, db *sql.DB, id int64) (UserContact, error) {
    var user UserContact
    err := db.QueryRowContext(ctx,
        "SELECT id, name, email, age FROM users WHERE id = $1", id,
    ).Scan(&user.ID, &user.Name, &user.Email, &user.Age)
    if err != nil {
        return UserContact{}, err
    }
    return user, nil
}
```

- Если строки с таким ID нет, `Scan` возвращает `sql.ErrNoRows`.
- Если строка найдена и `email IS NULL`, чтение проходит успешно и `Email.Valid == false`.
- Для пустой строки `Email.Valid == true` и `Email.String == ""`. Для записанного возраста `0` получим `Age.Valid == true` и `Age.Int64 == 0`.

Проверяйте `Valid` после успешного `Scan`. Чтение SQL `NULL` напрямую в `string` или `int64` приводит к ошибке преобразования. При записи параметра `sql.NullString{Valid: false}` передаёт SQL `NULL` через интерфейс `driver.Valuer`. Если используете `COALESCE(email, '')`, то намеренно сводите два разных состояния к пустой строке.

[Контракт Row.Scan](https://pkg.go.dev/database/sql#Row.Scan) · [Тип NullString](https://pkg.go.dev/database/sql#NullString) · [Преобразование значений в database/sql](https://go.dev/src/database/sql/convert.go)

### Работа с JSON в PostgreSQL

Пусть `users.metadata` имеет тип `jsonb` и допускает SQL `NULL`. Передавайте документ как текстовый параметр с явным приведением к `jsonb`. Значение `nil` записывает SQL `NULL`, а `json.RawMessage("null")` записывает JSON `null`.

```go
func saveJSON(ctx context.Context, db *sql.DB, name string, metadata json.RawMessage) error {
    var param any
    if metadata != nil {
        param = string(metadata)
    }
    _, err := db.ExecContext(ctx,
        "INSERT INTO users (name, metadata) VALUES ($1, $2::jsonb)", name, param)
    return err
}

func readJSON(ctx context.Context, db *sql.DB, id int64) (json.RawMessage, error) {
    var metadata sql.NullString
    err := db.QueryRowContext(ctx,
        "SELECT metadata::text FROM users WHERE id = $1", id).Scan(&metadata)
    if err != nil {
        return nil, err
    }
    if !metadata.Valid {
        return nil, nil
    }
    return json.RawMessage(metadata.String), nil
}
```

Если строки нет, `Scan` возвращает `sql.ErrNoRows`. Для SQL `NULL` существующей строки функция возвращает `nil, nil`. Для JSON `null` она возвращает байты текста `null`. PostgreSQL проверяет вход при приведении к `jsonb`. Тип `jsonb` не сохраняет исходное форматирование и порядок ключей объекта.

См. [типы JSON в PostgreSQL 19](https://www.postgresql.org/docs/19/datatype-json.html) и [sql.NullString](https://pkg.go.dev/database/sql#NullString).

### Работа с массивами в PostgreSQL

В примере `users.tags` имеет тип `text[]`. Непустые массивы одномерные, с нижней границей 1 и без `NULL` внутри. `pq.Array` кодирует слайс как параметр SQL и декодирует результат через `Scan`.

```go
func saveArray(ctx context.Context, db *sql.DB, userID int, tags []string) error {
    _, err := db.ExecContext(ctx,
        "UPDATE users SET tags = $1 WHERE id = $2",
        pq.Array(tags), userID,
    )
    return err
}

func readArray(ctx context.Context, db *sql.DB, userID int) ([]string, error) {
    var tags []string
    err := db.QueryRowContext(ctx,
        "SELECT tags FROM users WHERE id = $1",
        userID,
    ).Scan(pq.Array(&tags))
    return tags, err
}
```

- При записи `nil`-слайс передаёт SQL `NULL`, а `[]string{}` передаёт пустой массив. При чтении они возвращаются как `nil` и ненулевой слайс длины 0 соответственно. Если строки пользователя нет, `Scan` возвращает `sql.ErrNoRows`.
- Если внутри массива допустимы `NULL`-элементы, сканируйте в `[]sql.NullString` через `pq.Array(&tags)`. Поле `Valid` отличает `NULL` от пустой строки. При сканировании в `[]string` такой элемент вызовет ошибку.
- PostgreSQL поддерживает многомерные массивы и нижнюю границу индекса, отличную от 1. Сканирование через `pq.Array` эти случаи не поддерживает.

[Массивы PostgreSQL](https://www.postgresql.org/docs/current/arrays.html) · [lib/pq Array](https://pkg.go.dev/github.com/lib/pq#Array)

### Оптимизация запросов

Допустим, приложение часто ищет пользователя по `email`. Сначала проверьте существующие индексы и план запроса на репрезентативных данных. Если ограничение `UNIQUE (email)` уже создало подходящий индекс, второй индекс на ту же колонку добавит работу при записи. На маленькой таблице или при низкой селективности PostgreSQL может выбрать последовательное чтение даже при наличии индекса.

На большой таблице с активными записями создание индекса вынесите в миграцию. Для обычной непартиционированной таблицы PostgreSQL можно использовать:

```sql
CREATE INDEX CONCURRENTLY idx_users_email ON public.users (email);
```

Обычный `CREATE INDEX` блокирует запись в таблицу до конца построения. `CONCURRENTLY` позволяет продолжать запись, но делает дополнительные проходы и может ждать завершения транзакций. Запустите эту команду вне транзакционного блока, включая блок, который мог бы открыть инструмент миграций. После сбоя проверьте, не остался ли индекс в состоянии `INVALID`: такой индекс не используется для чтения, но может создавать накладные расходы при записи. Перед повторной миграцией устраните причину сбоя и удалите невалидный индекс либо перестройте его.

После миграции сравните планы и время операции. [Пример диагностики использует фиксированный SELECT с параметром](#практические-примеры-оптимизация-запросов-через-explain). `EXPLAIN ANALYZE` выполняет переданный запрос, поэтому ограничьте диагностический код заранее выбранным SQL.

[PostgreSQL — CREATE INDEX](https://www.postgresql.org/docs/current/sql-createindex.html) · [PostgreSQL — EXPLAIN](https://www.postgresql.org/docs/current/sql-explain.html)

### Работа с большими результатами

Для экспорта пользователей можно обрабатывать строки по одной, не собирая весь результат в слайс. Такой цикл ограничивает объём данных, который накапливает сам обработчик. Буферизация в драйвере и на сервере зависит от реализации и плана запроса.

```go
func streamLargeResults(
    ctx context.Context,
    db *sql.DB,
    consume func(context.Context, User) error,
) error {
    rows, err := db.QueryContext(ctx, "SELECT id, name FROM users")
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Name); err != nil {
            return err
        }
        if err := consume(ctx, user); err != nil {
            return err
        }
    }
    return rows.Err()
}
```

`defer rows.Close()` освобождает результат и при досрочном выходе. `Rows.Err()` отличает конец результата от ошибки чтения. Уже переданные обработчику строки могут быть записаны к моменту ошибки, поэтому экспорт с возможностью повтора должен учитывать частичный результат. Дедлайн задаёт вызывающий код. Для прерывания работы обработчик должен сам учитывать переданный контекст.

Пока результат открыт, запрос занимает соединение. При `SetMaxOpenConns(1)` обработчик, который сам обращается к той же `sql.DB`, может ждать это соединение до истечения дедлайна. Если обработка требует новых запросов или медленного внешнего вызова, читайте ограниченную страницу, закрывайте результат и только затем обрабатывайте данные. При таком обходе отдельно определите требования к снимку данных: несколько страниц могут прочитать разные состояния таблицы.

[Go — Rows](https://pkg.go.dev/database/sql#Rows) · [Go — управление пулом](https://go.dev/doc/database/manage-connections) · [Go — отмена запросов](https://go.dev/doc/database/cancel-operations)

### Pagination

```go
// Offset-based pagination
func getUsersPaginated(ctx context.Context, db *sql.DB, page, pageSize int) ([]User, error) {
    offset := (page - 1) * pageSize
    query := "SELECT id, name FROM users ORDER BY id LIMIT $1 OFFSET $2"

    rows, err := db.QueryContext(ctx, query, pageSize, offset)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var users []User
    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Name); err != nil {
            return nil, err
        }
        users = append(users, user)
    }

    if err := rows.Err(); err != nil {
        return nil, err
    }
    return users, nil
}

// Cursor-based pagination
func getUsersCursor(ctx context.Context, db *sql.DB, cursor int, limit int) ([]User, int, error) {
    query := "SELECT id, name FROM users WHERE id > $1 ORDER BY id LIMIT $2"

    rows, err := db.QueryContext(ctx, query, cursor, limit)
    if err != nil {
        return nil, 0, err
    }
    defer rows.Close()

    var users []User
    lastID := cursor
    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Name); err != nil {
            return nil, 0, err
        }
        users = append(users, user)
        lastID = user.ID
    }

    if err := rows.Err(); err != nil {
        return nil, cursor, err
    }
    return users, lastID, nil
}
```

В обоих примерах `id` — уникальный ключ, а вызывающий код заранее проверяет положительный размер страницы и допустимость смещения. При ошибке чтения функция возвращает ошибку целиком, чтобы клиент не принял обрезанную страницу за полную.

`OFFSET` позволяет перейти к произвольной странице, но сервер всё равно вычисляет пропускаемые строки. Вставка или удаление перед смещением между запросами может сдвинуть границу страницы. Запрос по курсору продолжает чтение после последнего полученного `id` и может использовать индекс по этому ключу. Для первого запроса передайте 0, если все ID положительные. Пустая страница сохраняет предыдущий курсор. Между запросами данные могут измениться, поэтому курсор сам по себе не фиксирует единый снимок.

[PostgreSQL — LIMIT и OFFSET](https://www.postgresql.org/docs/19/queries-limit.html) · [Go — Rows.Err](https://pkg.go.dev/database/sql#Rows.Err)

Если список отсортирован от новых записей к старым, передавайте в курсоре оба поля сортировки. В примере `created_at` задан как `NOT NULL`, а уникальный `id` различает записи с одинаковым временем. Для первой страницы опустите условие `WHERE`. Для следующих передайте время и ID последней строки плюс размер страницы.

```sql
SELECT id, name, created_at
FROM users
WHERE (created_at, id) < ($1, $2)
ORDER BY created_at DESC, id DESC
LIMIT $3;
```

Если предыдущая страница закончилась на ID 42, запись с тем же временем и ID 41 попадёт за курсор. Курсор из одного `created_at` с условием `created_at < $1` пропустил бы её. Сравнение пары подходит для двух полей, отсортированных по убыванию. При смешанных направлениях запишите условие явно по правилам каждого поля. Изменение поля сортировки между запросами всё ещё может привести к пропуску или повторному появлению строки.

[PostgreSQL — сравнение строк](https://www.postgresql.org/docs/19/functions-comparisons.html#ROW-WISE-COMPARISON)

### Repository Pattern

```go
type UserRepository interface {
    GetByID(id int) (*User, error)
    GetAll() ([]User, error)
    Create(user *User) error
    Update(user *User) error
    Delete(id int) error
}

type userRepository struct {
    db *sql.DB
}

func NewUserRepository(db *sql.DB) UserRepository {
    return &userRepository{db: db}
}

func (r *userRepository) GetByID(id int) (*User, error) {
    var user User
    err := r.db.QueryRow(
        "SELECT id, name, email FROM users WHERE id = $1",
        id,
    ).Scan(&user.ID, &user.Name, &user.Email)
    if err != nil {
        return nil, err
    }
    return &user, nil
}

func (r *userRepository) Create(user *User) error {
    err := r.db.QueryRow(
        "INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id",
        user.Name, user.Email,
    ).Scan(&user.ID)
    return err
}
```

### Unit of Work Pattern

В этом примере создание пользователя и его первой публикации образуют одну операцию. `sql.Tx` задаёт общую транзакционную границу.

```go
func createUserWithFirstPost(
    ctx context.Context,
    db *sql.DB,
    name, email, title string,
) (int64, error) {
    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return 0, err
    }
    defer tx.Rollback()

    var userID int64
    err = tx.QueryRowContext(ctx,
        `INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id`,
        name, email,
    ).Scan(&userID)
    if err != nil {
        return 0, err
    }

    _, err = tx.ExecContext(ctx,
        `INSERT INTO posts (user_id, title) VALUES ($1, $2)`,
        userID, title,
    )
    if err != nil {
        return 0, err
    }

    if err := tx.Commit(); err != nil {
        return 0, err
    }
    return userID, nil
}
```

Если вставка публикации нарушит ограничение, отложенный `Rollback` отменит и создание пользователя. Идентификатор возвращается только после успешного `Commit`. Если операции вынесены в репозитории, передайте им тот же `*sql.Tx`. Вызов через `*sql.DB` выполнится за пределами этой транзакции.

[Go — выполнение транзакций](https://go.dev/doc/database/execute-transactions)

### Практические примеры: Connection Pooling

```go
type DBManager struct {
    db *sql.DB
}

func NewDBManager(ctx context.Context, dsn string) (*DBManager, error) {
    db, err := sql.Open("postgres", dsn)
    if err != nil {
        return nil, err
    }

    // Настройка пула
    db.SetMaxOpenConns(25)
    db.SetMaxIdleConns(5)
    db.SetConnMaxLifetime(5 * time.Minute)

    pingCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()
    if err := db.PingContext(pingCtx); err != nil {
        _ = db.Close()
        return nil, err
    }

    return &DBManager{db: db}, nil
}

func (m *DBManager) Close() error {
    return m.db.Close()
}

func (m *DBManager) Stats() sql.DBStats {
    return m.db.Stats()
}
```

При неудачной проверке конструктор закрывает созданный пул. После успешного запуска вызывающий код закрывает его при завершении приложения.

### Практические примеры: Health Check

```go
func (m *DBManager) HealthCheck(ctx context.Context) error {
    ctx, cancel := context.WithTimeout(ctx, time.Second)
    defer cancel()

    return m.db.PingContext(ctx)
}
```

HTTP-обработчик передаёт `r.Context()`, чтобы проверка учитывала отмену запроса. Если пул исчерпан, ожидание соединения может израсходовать весь бюджет даже при работающей БД. Если без БД сервис не может обрабатывать трафик, используйте этот сигнал для readiness. Для liveness проверяйте состояние самого процесса, чтобы временная недоступность БД не приводила к перезапускам.

[Go — DB.PingContext](https://pkg.go.dev/database/sql#DB.PingContext) · [Kubernetes — пробы контейнера](https://kubernetes.io/docs/concepts/workloads/pods/probes/)

### Практические примеры: Query Builder

В списке пользователей клиент выбирает сортировку по имени или по времени создания. Код сопоставляет этот выбор с фиксированным SQL-фрагментом, а параметры фильтра и пагинации передаёт отдельно.

```go
func listUsers(ctx context.Context, db *sql.DB, domain string, order string, limit, offset int) (*sql.Rows, error) {
    var orderBy string
    switch order {
    case "", "newest":
        orderBy = "created_at DESC, id DESC"
    case "name":
        orderBy = "name ASC, id ASC"
    default:
        return nil, fmt.Errorf("unknown sort order: %q", order)
    }

    if limit < 1 || limit > 100 || offset < 0 {
        return nil, fmt.Errorf("invalid pagination: limit=%d, offset=%d", limit, offset)
    }

    query := `SELECT id, email, name, created_at
              FROM users
              WHERE split_part(email, '@', 2) = $1
              ORDER BY ` + orderBy + `
              LIMIT $2 OFFSET $3`

    return db.QueryContext(ctx, query, domain, limit, offset)
}
```

`ORDER BY $1` передаёт значение выражения, поэтому через него нельзя выбрать имя столбца или направление сортировки. В примере допустимые варианты перечислены в `switch`, а уникальный `id` разрешает совпадения по первому ключу сортировки. Ограничение в 100 строк задаёт приложение. Вызывающий код закрывает `Rows` и проверяет `Rows.Err()` после обхода. [Сортировка в PostgreSQL](https://www.postgresql.org/docs/19/queries-order.html) · [Параметры PostgreSQL](https://www.postgresql.org/docs/19/sql-prepare.html) · [Чтение строк в Go](https://go.dev/doc/database/querying#querying-for-multiple-rows)

### Практические примеры: Database Migrations

В PostgreSQL добавим необязательное поле для имени, которое видит пользователь. Старая версия сервиса может продолжать записывать строки, если её запросы перечисляют столбцы явно.

`migrations/000007_add_display_name.up.sql`

```sql
ALTER TABLE public.users ADD COLUMN display_name text;
```

`migrations/000007_add_display_name.down.sql`

```sql
ALTER TABLE public.users DROP COLUMN display_name;
```

Запустите `Up()` из отдельной задачи деплоя для целевой базы. `golang-migrate` применяет ожидающие версии по порядку и хранит текущую версию. При повторном запуске с тем же набором файлов и чистым состоянием ожидайте `migrate.ErrNoChange`. Проверяйте совместимость старого приложения с новой схемой до раскатки. Обратная миграция удаляет и столбец, и записанные в него данные, поэтому при откате приложения обычно можно оставить добавленный совместимый столбец.

Документация: [формат миграций golang-migrate](https://github.com/golang-migrate/migrate/blob/master/MIGRATIONS.md).

### Практические примеры: Repository паттерн с database/sql

```go
type UserRepository struct {
    db *sql.DB
}

func NewUserRepository(db *sql.DB) *UserRepository {
    return &UserRepository{db: db}
}

func (r *UserRepository) Create(ctx context.Context, user *User) error {
    query := `INSERT INTO users (email, name, created_at)
              VALUES ($1, $2, $3) RETURNING id`
    err := r.db.QueryRowContext(ctx, query, user.Email, user.Name, time.Now()).
        Scan(&user.ID)
    return err
}

func (r *UserRepository) FindByID(ctx context.Context, id int) (*User, error) {
    var user User
    query := `SELECT id, email, name, created_at FROM users WHERE id = $1`
    err := r.db.QueryRowContext(ctx, query, id).
        Scan(&user.ID, &user.Email, &user.Name, &user.CreatedAt)
    if err == sql.ErrNoRows {
        return nil, ErrNotFound
    }
    return &user, err
}

func (r *UserRepository) Update(ctx context.Context, user *User) error {
    query := `UPDATE users SET email = $1, name = $2 WHERE id = $3`
    result, err := r.db.ExecContext(ctx, query, user.Email, user.Name, user.ID)
    if err != nil {
        return err
    }

    affected, err := result.RowsAffected()
    if err != nil {
        return err
    }

    if affected == 0 {
        return ErrNotFound
    }

    return nil
}

func (r *UserRepository) Delete(ctx context.Context, id int) error {
    query := `DELETE FROM users WHERE id = $1`
    result, err := r.db.ExecContext(ctx, query, id)
    if err != nil {
        return err
    }

    affected, err := result.RowsAffected()
    if err != nil {
        return err
    }

    if affected == 0 {
        return ErrNotFound
    }

    return nil
}

func (r *UserRepository) FindAll(ctx context.Context, limit, offset int) ([]User, error) {
    query := `SELECT id, email, name, created_at
              FROM users
              ORDER BY created_at DESC, id DESC
              LIMIT $1 OFFSET $2`
    rows, err := r.db.QueryContext(ctx, query, limit, offset)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var users []User
    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Email, &user.Name, &user.CreatedAt); err != nil {
            return nil, err
        }
        users = append(users, user)
    }

    if err := rows.Err(); err != nil {
        return nil, err
    }
    return users, nil
}
```

`FindAll` возвращает страницу только после успешного чтения всех её строк. Если чтение оборвалось посередине, вызывающий код получает ошибку и может повторить запрос. Уникальный `id` фиксирует порядок пользователей с одинаковым `created_at`. При вставке или удалении строк между запросами страницы с `OFFSET` всё равно могут смещаться. [Чтение строк в Go](https://go.dev/doc/database/querying#querying-for-multiple-rows) · [LIMIT и OFFSET в PostgreSQL](https://www.postgresql.org/docs/19/queries-limit.html)

### Практические примеры: Batch операции с транзакциями

```go
func BatchInsertUsers(ctx context.Context, db *sql.DB, users []User) error {
    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return err
    }
    defer tx.Rollback()

    stmt, err := tx.PrepareContext(ctx,
        `INSERT INTO users (email, name, created_at) VALUES ($1, $2, $3)`)
    if err != nil {
        return err
    }
    defer stmt.Close()

    for _, user := range users {
        if _, err := stmt.ExecContext(ctx, user.Email, user.Name, time.Now()); err != nil {
            return err
        }
    }

    return tx.Commit()
}

func BatchUpdateUsers(ctx context.Context, db *sql.DB, updates map[int]User) error {
    if len(updates) == 0 {
        return nil
    }

    ids := make([]int, 0, len(updates))
    for id := range updates {
        ids = append(ids, id)
    }
    sort.Ints(ids)

    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return err
    }
    defer tx.Rollback()

    stmt, err := tx.PrepareContext(ctx,
        `UPDATE users SET email = $1, name = $2 WHERE id = $3`)
    if err != nil {
        return err
    }
    defer stmt.Close()

    for _, id := range ids {
        user := updates[id]
        if _, err := stmt.ExecContext(ctx, user.Email, user.Name, id); err != nil {
            return err
        }
    }

    return tx.Commit()
}
```

Для `sort.Ints` подключите пакет `sort`. Если две транзакции обновляют пользователей 10 и 20 в разном порядке, каждая может удерживать блокировку строки, нужную другой. Единый порядок по ID устраняет этот сценарий, когда все участники захватывают те же блокировки в том же порядке. Другие операции, например выполняемые триггерами, могут захватывать дополнительные блокировки. [Порядок обхода map в Go](https://go.dev/ref/spec#For_range) · [Взаимные блокировки в PostgreSQL](https://www.postgresql.org/docs/19/explicit-locking.html#LOCKING-DEADLOCKS)

### Практические примеры: Запросы с динамическими условиями

Для PostgreSQL формируйте условия из заранее заданных фрагментов. Значения фильтров передавайте отдельно через аргументы `QueryContext`.

```go
func queryUsers(ctx context.Context, db *sql.DB, email *string, minAge *int) (*sql.Rows, error) {
    query := `SELECT id, email, age FROM users`
    var conditions []string
    var args []any

    if email != nil {
        args = append(args, *email)
        conditions = append(conditions, fmt.Sprintf("email = $%d", len(args)))
    }
    if minAge != nil {
        args = append(args, *minAge)
        conditions = append(conditions, fmt.Sprintf("age >= $%d", len(args)))
    }
    if len(conditions) > 0 {
        query += " WHERE " + strings.Join(conditions, " AND ")
    }

    return db.QueryContext(ctx, query, args...)
}
```

Если задан только минимальный возраст, условие будет `age >= $1`. Если заданы оба фильтра, получится `email = $1 AND age >= $2`. `nil` означает отсутствие фильтра, поэтому нулевой возраст можно передать как значение. Без фильтров запрос читает всю таблицу. Вызывающий код закрывает `Rows` и проверяет `Rows.Err()` после обхода. Параметры PostgreSQL обозначают значения. Имена таблиц и столбцов для динамического запроса выбирайте из заранее заданных SQL-фрагментов. [Параметры PostgreSQL](https://www.postgresql.org/docs/19/sql-prepare.html) · [Передача значений в Go](https://go.dev/doc/database/sql-injection)

### Практические примеры: Миграции с проверкой

Для большой обычной таблицы PostgreSQL можно строить индекс без блокировки записи на всё время построения. Выделите команду в отдельный файл версии и выполняйте её вне явного блока `BEGIN … COMMIT`. Проверьте, что выбранный драйвер и настройки запуска не оборачивают файл в транзакцию.

`migrations/000008_index_users_created_at.up.sql`

```sql
CREATE INDEX CONCURRENTLY users_created_at_idx ON public.users (created_at);
```

Если запуск прервался и инструмент сообщил о грязной версии, сначала остановите повторные запуски и проверьте состояние схемы. Например, после неудачного построения индекс может остаться в каталоге с `indisvalid = false`.

```sql
SELECT i.indisvalid, pg_get_indexdef(i.indexrelid) AS definition
FROM pg_index AS i
WHERE i.indrelid = 'public.users'::regclass
  AND i.indexrelid = to_regclass('public.users_created_at_idx');
```

Отсутствие строки означает, что проверка не нашла этот индекс на указанной таблице. Если индекс валиден, сравните его определение с миграцией. Если остался невалидный индекс, определите причину сбоя и спланируйте его удаление перед повторным построением. Для этого обычного индекса можно использовать `DROP INDEX CONCURRENTLY` отдельной командой вне транзакционного блока. Согласуйте номер версии с фактической схемой перед `Force`. Сам вызов меняет метаданные мигратора и не выполняет SQL. `IF NOT EXISTS` здесь может скрыть невалидный индекс или совпадение имени при другом определении.

Документация: [CREATE INDEX](https://www.postgresql.org/docs/19/sql-createindex.html), [DROP INDEX](https://www.postgresql.org/docs/19/sql-dropindex.html), [pg_index](https://www.postgresql.org/docs/19/catalog-pg-index.html).

### Практические примеры: Оптимизация запросов

Если поиск пользователя по email замедлился, сначала измерьте время операции в приложении. Для запроса, возвращающего несколько строк, измерение должно охватывать чтение результата. При превышении выбранного порога пишите в журнал имя операции, например `users.find_by_email`, длительность и результат. Значения параметров и необработанный SQL могут содержать персональные данные.

Сравните измерение приложения с планом PostgreSQL из раздела «Практические примеры: Оптимизация запросов через EXPLAIN». Время в приложении включает ожидание соединения из пула, сетевой обмен и обработку результата. Если серверное выполнение быстрое, проверьте эти этапы отдельно.

## Лучшие практики

1. **Переиспользуйте пул.** Обычно `*sql.DB` создают один раз на конфигурацию подключения и закрывают при остановке приложения. Согласуйте лимит открытых соединений всех реплик приложения с бюджетом БД. Следите за ростом `WaitCount` и `WaitDuration` в `DB.Stats()`.
2. **Передавайте значения параметрами.** Для этого достаточно аргументов `QueryContext` или `ExecContext`. Явный `PrepareContext` позволяет переиспользовать `Stmt` для повторяющегося SQL. Оцените выигрыш на своей нагрузке и закройте `Stmt`, когда он больше не нужен.
3. **Освобождайте ресурсы и проверяйте ошибки.** Закрывайте `Rows` при досрочном выходе, проверяйте `Rows.Err()` после обхода, возвращайте выделенный через `DB.Conn` ресурс в пул через `Conn.Close()`. Передавайте контекст операции, чтобы ограничить время ожидания соединения и запроса.
4. **Задавайте транзакционную границу явно.** Если несколько операций должны фиксироваться вместе, выполняйте их через один `Tx` и проверяйте результат `Commit`. Для массовых операций выбирайте размер пачки с учётом времени транзакции и допустимости частичной фиксации.
5. **Измеряйте стоимость запросов.** Считайте запросы на бизнес-операцию, чтобы заметить N+1. Сравнивайте пакетную загрузку со связями через `JOIN`, учитывая кратность результата. Проверяйте план на репрезентативных данных перед добавлением индекса и оформляйте изменение схемы миграцией.
6. **Выбирайте границу слоя доступа по задаче.** Repository оправдан, когда он изолирует детали хранения и упрощает проверку доменных сценариев.

[Go — управление пулом](https://go.dev/doc/database/manage-connections) · [Go — параметры SQL](https://go.dev/doc/database/sql-injection) · [Go — подготовленные запросы](https://go.dev/doc/database/prepared-statements) · [Go — транзакции](https://go.dev/doc/database/execute-transactions)

### Практические примеры: Оптимизация запросов через EXPLAIN

Для поиска пользователя по email начните с обычного `EXPLAIN`, чтобы увидеть оценки планировщика без выполнения запроса. Если нужны наблюдаемые значения, измерьте конкретный запрос на среде с репрезентативными данными. `EXPLAIN ANALYZE` исполняет запрос, поэтому его стоимость и побочные эффекты реальны. Пример ограничен известным `SELECT`, а email передаётся как параметр PostgreSQL.

```go
func ExplainUserLookup(ctx context.Context, db *sql.DB, email string) ([]string, error) {
    ctx, cancel := context.WithTimeout(ctx, 2*time.Second)
    defer cancel()

    const query = `EXPLAIN (ANALYZE, BUFFERS)
        SELECT id, email FROM public.users WHERE email = $1`
    rows, err := db.QueryContext(ctx, query, email)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var plan []string
    for rows.Next() {
        var line string
        if err := rows.Scan(&line); err != nil {
            return nil, err
        }
        plan = append(plan, line)
    }
    if err := rows.Err(); err != nil {
        return nil, err
    }
    return plan, nil
}
```

Две секунды здесь служат примером бюджета диагностики. Поддержка отмены уже отправленного запроса зависит от драйвера. План может включать значение фильтра, поэтому просматривайте его в защищённом канале. Сопоставьте оценку `rows` с `actual rows`, учитывая, что при нескольких `loops` фактическое число строк показано в среднем за запуск узла. Счётчик `shared read` означает чтение блока в общие буферы PostgreSQL и может включать чтение из кэша ОС.

См. [EXPLAIN в PostgreSQL 19](https://www.postgresql.org/docs/19/sql-explain.html) и [QueryContext в database/sql](https://pkg.go.dev/database/sql#DB.QueryContext).

### Практические примеры: Кэширование результатов запросов

Для небольшого справочника можно хранить результат чтения в памяти процесса. Запись содержит значение и срок действия. Если приложение дважды записывает один ключ, второе значение получает собственный срок. За чтение, обновление и удаление отвечает одна блокировка.

```go
type cacheEntry struct {
    value     string
    expiresAt time.Time
}

type QueryCache struct {
    mu      sync.Mutex
    entries map[string]cacheEntry
}

func (c *QueryCache) Get(key string) (string, bool) {
    c.mu.Lock()
    defer c.mu.Unlock()

    entry, ok := c.entries[key]
    if !ok {
        return "", false
    }
    if !time.Now().Before(entry.expiresAt) {
        delete(c.entries, key)
        return "", false
    }
    return entry.value, true
}

func (c *QueryCache) Set(key, value string, ttl time.Duration) {
    c.mu.Lock()
    defer c.mu.Unlock()

    if ttl <= 0 {
        delete(c.entries, key)
        return
    }
    if c.entries == nil {
        c.entries = make(map[string]cacheEntry)
    }
    c.entries[key] = cacheEntry{value: value, expiresAt: time.Now().Add(ttl)}
}
```

При промахе приложение читает БД и кеширует успешный результат. Ключ должен учитывать все параметры запроса и область данных, например пару из идентификатора арендатора и идентификатора записи. При обновлении БД TTL оставляет окно, в котором кеш может вернуть прежнее значение. Одновременные промахи также могут запустить несколько одинаковых запросов.

Пример удаляет просроченную запись при следующем чтении её ключа. Для множества меняющихся ключей нужна политика, ограничивающая память, поскольку записи без повторного чтения остаются в map. Для сравнения времени внутри процесса `time.Now().Add(ttl)` сохраняет показание монотонных часов. [Документация time](https://pkg.go.dev/time#hdr-Monotonic_Clocks) и [документация sync.Mutex](https://pkg.go.dev/sync#Mutex).


## Решение проблем

**Запрос зависает после открытия транзакции.** Допустим, у пула `MaxOpenConns = 1`. Вызов `BeginTx` занимает единственное соединение. Если вспомогательная функция внутри транзакционного сценария обращается к `db.QueryRowContext(...).Scan(...)`, она ждёт ещё одно соединение, а вызывающий код ждёт её результата перед `Commit`. При заданном дедлайне ожидание закончится ошибкой контекста. При большем лимите тот же сценарий возможен, когда все соединения заняты такими транзакциями.

Снимите `db.Stats()` несколько раз во время сбоя. Если `InUse` держится у лимита, а `WaitCount` растёт, операции ждут соединение из пула. Эти метрики сами по себе не показывают причину. Проверьте, через какой объект выполняется зависший запрос, как долго открыты транзакции и закрываются ли `Rows` при раннем выходе.

Если операция должна видеть изменения текущей транзакции, передайте вспомогательной функции `*sql.Tx` или интерфейс с нужными методами и выполняйте запрос через транзакцию. Увеличение пула может отсрочить исчерпание. Вызов через `*sql.DB` всё равно выполняется вне этой транзакции и может видеть другое состояние данных.

[Go — управление соединениями](https://go.dev/doc/database/manage-connections) · [Go — выполнение транзакций](https://go.dev/doc/database/execute-transactions)

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа с базами данных в Go предоставляет мощные инструменты для создания приложений, работающих с данными. Понимание **database**/**sql**, **sqlx**, **GORM**, транзакций, миграций, **Repository** паттерна, оптимизации запросов, кэширования и практических техник критично для создания эффективных приложений. Правильное использование этих инструментов позволяет создавать масштабируемые, производительные приложения, которые эффективно работают с базами данных, обеспечивают целостность данных, оптимальную производительность и быструю реакцию на запросы.

## Дополнительные ресурсы

- [Go database/sql Documentation](https://pkg.go.dev/database/sql)
- [sqlx Documentation](https://pkg.go.dev/github.com/jmoiron/sqlx)
- [GORM Documentation](https://gorm.io/docs/)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
