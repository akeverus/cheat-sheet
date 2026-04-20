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
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
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
    "database/sql"
    _ "github.com/lib/pq"  // PostgreSQL драйвер
)

func main() {
    db, err := sql.Open("postgres", "user=postgres dbname=mydb sslmode=disable")
    if err != nil {
        log.Fatal(err)
    }
    defer db.Close()

    // Проверка подключения
    if err := db.Ping(); err != nil {
        log.Fatal(err)
    }
}
```

### Выполнение запросов

```go
import "database/sql"

// Выполнение запроса без результата
_, err := db.Exec("CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT)")

// Выполнение запроса с параметрами
_, err := db.Exec("INSERT INTO users (name) VALUES ($1)", "Alice")

// Выполнение запроса с результатом
rows, err := db.Query("SELECT id, name FROM users")
if err != nil {
    log.Fatal(err)
}
defer rows.Close()

for rows.Next() {
    var id int
    var name string
    if err := rows.Scan(&id, &name); err != nil {
        log.Fatal(err)
    }
    fmt.Printf("ID: %d, Name: %s\n", id, name)
}
```

### Запрос одной строки

```go
var user User
err := db.QueryRow("SELECT id, name FROM users WHERE id = $1", 1).
    Scan(&user.ID, &user.Name)
if err != nil {
    log.Fatal(err)
}
```

### Подготовленные запросы

```go
stmt, err := db.Prepare("SELECT id, name FROM users WHERE id = $1")
if err != nil {
    log.Fatal(err)
}
defer stmt.Close()

var user User
err = stmt.QueryRow(1).Scan(&user.ID, &user.Name)
if err != nil {
    log.Fatal(err)
}
```

## sqlx

Пакет `sqlx` расширяет `database/sql` дополнительными возможностями.

### Подключение

```go
import "github.com/jmoiron/sqlx"

db, err := sqlx.Connect("postgres", "user=postgres dbname=mydb sslmode=disable")
if err != nil {
    log.Fatal(err)
}
defer db.Close()
```

### Запросы с автоматическим маппингом

```go
type User struct {
    ID   int    `db:"id"`
    Name string `db:"name"`
}

// Запрос одной строки
var user User
err := db.Get(&user, "SELECT * FROM users WHERE id = $1", 1)

// Запрос множества строк
var users []User
err := db.Select(&users, "SELECT * FROM users")
```

### Именованные запросы

```go
type User struct {
    ID   int    `db:"id"`
    Name string `db:"name"`
}

// Именованные параметры
query := "SELECT * FROM users WHERE id = :id AND name = :name"
namedQuery, args, _ := sqlx.Named(query, map[string]interface{}{
    "id":   1,
    "name": "Alice",
})

var user User
err := db.Get(&user, db.Rebind(namedQuery), args...)
```

## GORM

**GORM** — это популярный **ORM** для Go.

### Подключение

```go
import (
    "gorm.io/driver/postgres"
    "gorm.io/gorm"
)

dsn := "host=localhost user=postgres dbname=mydb sslmode=disable"
db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
if err != nil {
    log.Fatal(err)
}
```

### Определение моделей

```go
type User struct {
    ID   uint   `gorm:"primaryKey"`
    Name string
    Email string `gorm:"uniqueIndex"`
}

// Автоматическая миграция
db.AutoMigrate(&User{})
```

### CRUD операции

```go
// Создание
user := User{Name: "Alice", Email: "alice@example.com"}
db.Create(&user)

// Чтение
var user User
db.First(&user, 1)  // по ID
db.First(&user, "name = ?", "Alice")  // по условию

// Обновление
db.Model(&user).Update("name", "Bob")
db.Model(&user).Updates(User{Name: "Bob", Email: "bob@example.com"})

// Удаление
db.Delete(&user)
```

### Запросы

```go
// Where условия
var users []User
db.Where("name = ?", "Alice").Find(&users)
db.Where("age > ?", 18).Find(&users)

// Сортировка
db.Order("name desc").Find(&users)

// Лимит и оффсет
db.Limit(10).Offset(20).Find(&users)

// Выбор полей
db.Select("name", "email").Find(&users)
```

## Транзакции

Транзакции обеспечивают атомарность операций.

### database/sql транзакции

```go
tx, err := db.Begin()
if err != nil {
    log.Fatal(err)
}

defer func() {
    if err != nil {
        tx.Rollback()
        return
    }
    tx.Commit()
}()

_, err = tx.Exec("INSERT INTO users (name) VALUES ($1)", "Alice")
if err != nil {
    return err
}

_, err = tx.Exec("INSERT INTO users (name) VALUES ($1)", "Bob")
if err != nil {
    return err
}
```

### GORM транзакции

```go
db.Transaction(func(tx *gorm.DB) error {
    if err := tx.Create(&user1).Error; err != nil {
        return err
    }

    if err := tx.Create(&user2).Error; err != nil {
        return err
    }

    return nil
})
```

## Миграции

Миграции позволяют управлять схемой базы данных.

### Использование migrate

```go
import "github.com/golang-migrate/migrate/v4"

m, err := migrate.New(
    "file://migrations",
    "postgres://user:pass@localhost/dbname?sslmode=disable",
)
if err != nil {
    log.Fatal(err)
}

// Применение миграций
if err := m.Up(); err != nil {
    log.Fatal(err)
}
```

### GORM миграции

```go
// Автоматическая миграция
db.AutoMigrate(&User{}, &Post{})

// Ручная миграция
db.Migrator().CreateTable(&User{})
db.Migrator().AddColumn(&User{}, "Email")
db.Migrator().DropColumn(&User{}, "Email")
```

### Настройка пула соединений

```go
func configureConnectionPool(db *sql.DB) {
    // Максимальное количество открытых соединений
    db.SetMaxOpenConns(25)

    // Максимальное количество неактивных соединений
    db.SetMaxIdleConns(5)

    // Максимальное время жизни соединения
    db.SetConnMaxLifetime(5 * time.Minute)

    // Максимальное время простоя соединения
    db.SetConnMaxIdleTime(10 * time.Minute)
}
```

### Batch операции

```go
// Batch insert с database/sql
func batchInsert(db *sql.DB, users []User) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    stmt, err := tx.Prepare("INSERT INTO users (name, email) VALUES ($1, $2)")
    if err != nil {
        return err
    }
    defer stmt.Close()

    for _, user := range users {
        _, err := stmt.Exec(user.Name, user.Email)
        if err != nil {
            return err
        }
    }

    return tx.Commit()
}
```

### Batch операции с sqlx

```go
func batchInsertWithSqlx(db *sqlx.DB, users []User) error {
    query := `INSERT INTO users (name, email) VALUES (:name, :email)`
    _, err := db.NamedExec(query, users)
    return err
}
```

### Batch операции с GORM

```go
func batchInsertWithGORM(db *gorm.DB, users []User) error {
    return db.CreateInBatches(users, 100).Error
}
```

### Сложные запросы с JOIN

```go
// database/sql
func getUsersWithPosts(db *sql.DB) ([]UserWithPosts, error) {
    query := `
        SELECT u.id, u.name, p.id, p.title
        FROM users u
        LEFT JOIN posts p ON u.id = p.user_id
        ORDER BY u.id, p.id
    `

    rows, err := db.Query(query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var results []UserWithPosts
    currentUser := &UserWithPosts{}

    for rows.Next() {
        var userID int
        var userName string
        var postID sql.NullInt64
        var postTitle sql.NullString

        if err := rows.Scan(&userID, &userName, &postID, &postTitle); err != nil {
            return nil, err
        }

        if currentUser.ID != userID {
            if currentUser.ID != 0 {
                results = append(results, *currentUser)
            }
            currentUser = &UserWithPosts{
                ID:   userID,
                Name: userName,
                Posts: []Post{},
            }
        }

        if postID.Valid {
            currentUser.Posts = append(currentUser.Posts, Post{
                ID:    int(postID.Int64),
                Title: postTitle.String,
            })
        }
    }

    if currentUser.ID != 0 {
        results = append(results, *currentUser)
    }

    return results, nil
}
```

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

func getUsersWithPostsGORM(db *gorm.DB) ([]User, error) {
    var users []User
    err := db.Preload("Posts").Find(&users).Error
    return users, err
}
```

### Работа с NULL значениями

```go
import "database/sql"

type User struct {
    ID    int
    Name  string
    Email sql.NullString
    Age   sql.NullInt64
}

func handleNullValues(db *sql.DB) {
    var user User
    err := db.QueryRow("SELECT id, name, email, age FROM users WHERE id = $1", 1).
        Scan(&user.ID, &user.Name, &user.Email, &user.Age)
    if err != nil {
        log.Fatal(err)
    }

    if user.Email.Valid {
        fmt.Println("Email:", user.Email.String)
    } else {
        fmt.Println("Email is NULL")
    }

    if user.Age.Valid {
        fmt.Println("Age:", user.Age.Int64)
    }
}
```

### Работа с JSON в PostgreSQL

```go
// Сохранение JSON
type User struct {
    ID    int
    Name  string
    Metadata json.RawMessage
}

func saveJSON(db *sql.DB, user User) error {
    _, err := db.Exec(
        "INSERT INTO users (name, metadata) VALUES ($1, $2)",
        user.Name, user.Metadata,
    )
    return err
}

// Чтение JSON
func readJSON(db *sql.DB, id int) (*User, error) {
    var user User
    err := db.QueryRow(
        "SELECT id, name, metadata FROM users WHERE id = $1",
        id,
    ).Scan(&user.ID, &user.Name, &user.Metadata)
    return &user, err
}
```

### Работа с массивами в PostgreSQL

```go
// Сохранение массива
func saveArray(db *sql.DB, userID int, tags []string) error {
    _, err := db.Exec(
        "UPDATE users SET tags = $1 WHERE id = $2",
        pq.Array(tags), userID,
    )
    return err
}

// Чтение массива
func readArray(db *sql.DB, userID int) ([]string, error) {
    var tags []string
    err := db.QueryRow(
        "SELECT tags FROM users WHERE id = $1",
        userID,
    ).Scan(pq.Array(&tags))
    return tags, err
}
```

### Оптимизация запросов

```go
// Использование индексов
func createIndexes(db *sql.DB) error {
    _, err := db.Exec("CREATE INDEX idx_users_email ON users(email)")
    if err != nil {
        return err
    }

    _, err = db.Exec("CREATE INDEX idx_users_name ON users(name)")
    return err
}

// EXPLAIN для анализа запросов
func explainQuery(db *sql.DB, query string) error {
    rows, err := db.Query("EXPLAIN ANALYZE " + query)
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var plan string
        if err := rows.Scan(&plan); err != nil {
            return err
        }
        fmt.Println(plan)
    }
    return nil
}
```

### Работа с большими результатами

```go
// Streaming больших результатов
func streamLargeResults(db *sql.DB, callback func(User) error) error {
    rows, err := db.Query("SELECT id, name FROM users")
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Name); err != nil {
            return err
        }

        if err := callback(user); err != nil {
            return err
        }
    }

    return rows.Err()
}
```

### Pagination

```go
// Offset-based pagination
func getUsersPaginated(db *sql.DB, page, pageSize int) ([]User, error) {
    offset := (page - 1) * pageSize
    query := "SELECT id, name FROM users ORDER BY id LIMIT $1 OFFSET $2"

    rows, err := db.Query(query, pageSize, offset)
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

    return users, nil
}

// Cursor-based pagination
func getUsersCursor(db *sql.DB, cursor int, limit int) ([]User, int, error) {
    query := "SELECT id, name FROM users WHERE id > $1 ORDER BY id LIMIT $2"

    rows, err := db.Query(query, cursor, limit)
    if err != nil {
        return nil, 0, err
    }
    defer rows.Close()

    var users []User
    var lastID int
    for rows.Next() {
        var user User
        if err := rows.Scan(&user.ID, &user.Name); err != nil {
            return nil, 0, err
        }
        users = append(users, user)
        lastID = user.ID
    }

    return users, lastID, nil
}
```

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

```go
type UnitOfWork struct {
    db     *sql.DB
    tx     *sql.Tx
    users  UserRepository
    posts  PostRepository
}

func NewUnitOfWork(db *sql.DB) (*UnitOfWork, error) {
    tx, err := db.Begin()
    if err != nil {
        return nil, err
    }

    return &UnitOfWork{
        db:    db,
        tx:    tx,
        users: NewUserRepositoryWithTx(tx),
        posts: NewPostRepositoryWithTx(tx),
    }, nil
}

func (uow *UnitOfWork) Commit() error {
    return uow.tx.Commit()
}

func (uow *UnitOfWork) Rollback() error {
    return uow.tx.Rollback()
}
```

### Практические примеры: Connection Pooling

```go
type DBManager struct {
    db *sql.DB
}

func NewDBManager(dsn string) (*DBManager, error) {
    db, err := sql.Open("postgres", dsn)
    if err != nil {
        return nil, err
    }

    // Настройка пула
    db.SetMaxOpenConns(25)
    db.SetMaxIdleConns(5)
    db.SetConnMaxLifetime(5 * time.Minute)

    if err := db.Ping(); err != nil {
        return nil, err
    }

    return &DBManager{db: db}, nil
}

func (m *DBManager) Stats() sql.DBStats {
    return m.db.Stats()
}
```

### Практические примеры: Health Check

```go
func (m *DBManager) HealthCheck() error {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    return m.db.PingContext(ctx)
}
```

### Практические примеры: Query Builder

```go
type QueryBuilder struct {
    table   string
    where   []string
    orderBy string
    limit   int
    offset  int
}

func NewQueryBuilder(table string) *QueryBuilder {
    return &QueryBuilder{table: table}
}

func (qb *QueryBuilder) Where(condition string) *QueryBuilder {
    qb.where = append(qb.where, condition)
    return qb
}

func (qb *QueryBuilder) OrderBy(field string) *QueryBuilder {
    qb.orderBy = field
    return qb
}

func (qb *QueryBuilder) Limit(n int) *QueryBuilder {
    qb.limit = n
    return qb
}

func (qb *QueryBuilder) Build() string {
    query := "SELECT * FROM " + qb.table

    if len(qb.where) > 0 {
        query += " WHERE " + strings.Join(qb.where, " AND ")
    }

    if qb.orderBy != "" {
        query += " ORDER BY " + qb.orderBy
    }

    if qb.limit > 0 {
        query += fmt.Sprintf(" LIMIT %d", qb.limit)
    }

    if qb.offset > 0 {
        query += fmt.Sprintf(" OFFSET %d", qb.offset)
    }

    return query
}
```

### Практические примеры: Database Migrations

```go
type Migration struct {
    Version int
    Up      func(*sql.DB) error
    Down    func(*sql.DB) error
}

type Migrator struct {
    db         *sql.DB
    migrations []Migration
}

func NewMigrator(db *sql.DB) *Migrator {
    return &Migrator{
        db:         db,
        migrations: []Migration{},
    }
}

func (m *Migrator) Add(migration Migration) {
    m.migrations = append(m.migrations, migration)
}

func (m *Migrator) Up() error {
    for _, migration := range m.migrations {
        if err := migration.Up(m.db); err != nil {
            return err
        }
    }
    return nil
}
```

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
              ORDER BY created_at DESC
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

    return users, rows.Err()
}
```

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

    for id, user := range updates {
        if _, err := stmt.ExecContext(ctx, user.Email, user.Name, id); err != nil {
            return err
        }
    }

    return tx.Commit()
}
```

### Практические примеры: Запросы с динамическими условиями

```go
type QueryBuilder struct {
    conditions []string
    args       []interface{}
}

func NewQueryBuilder() *QueryBuilder {
    return &QueryBuilder{
        conditions: make([]string, 0),
        args:       make([]interface{}, 0),
    }
}

func (qb *QueryBuilder) Where(condition string, arg interface{}) *QueryBuilder {
    qb.conditions = append(qb.conditions, condition)
    qb.args = append(qb.args, arg)
    return qb
}

func (qb *QueryBuilder) Build(baseQuery string) (string, []interface{}) {
    if len(qb.conditions) > 0 {
        baseQuery += " WHERE " + strings.Join(qb.conditions, " AND ")
    }
    return baseQuery, qb.args
}

// Использование
builder := NewQueryBuilder()
query := "SELECT * FROM users"

if email != "" {
    builder.Where("email = $?", email)
}
if age > 0 {
    builder.Where("age >= $?", age)
}

finalQuery, args := builder.Build(query)
rows, err := db.Query(finalQuery, args...)
```

### Практические примеры: Миграции с проверкой

```go
type Migration struct {
    Version int
    Name    string
    Up      string
    Down    string
}

type MigrationManager struct {
    db         *sql.DB
    migrations []Migration
}

func (mm *MigrationManager) EnsureMigrationsTable() error {
    query := `CREATE TABLE IF NOT EXISTS schema_migrations (
        version INTEGER PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )`
    _, err := mm.db.Exec(query)
    return err
}

func (mm *MigrationManager) GetAppliedMigrations() (map[int]bool, error) {
    rows, err := mm.db.Query("SELECT version FROM schema_migrations")
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    applied := make(map[int]bool)
    for rows.Next() {
        var version int
        if err := rows.Scan(&version); err != nil {
            return nil, err
        }
        applied[version] = true
    }

    return applied, rows.Err()
}

func (mm *MigrationManager) ApplyMigration(migration Migration) error {
    tx, err := mm.db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    if _, err := tx.Exec(migration.Up); err != nil {
        return fmt.Errorf("migration %d (%s) failed: %w",
            migration.Version, migration.Name, err)
    }

    if _, err := tx.Exec(
        "INSERT INTO schema_migrations (version, name) VALUES ($1, $2)",
        migration.Version, migration.Name); err != nil {
        return err
    }

    return tx.Commit()
}

func (mm *MigrationManager) Migrate() error {
    if err := mm.EnsureMigrationsTable(); err != nil {
        return err
    }

    applied, err := mm.GetAppliedMigrations()
    if err != nil {
        return err
    }

    for _, migration := range mm.migrations {
        if applied[migration.Version] {
            continue
        }

        if err := mm.ApplyMigration(migration); err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: Оптимизация запросов

```go
// Использование EXPLAIN для анализа запросов
func ExplainQuery(db *sql.DB, query string, args ...interface{}) error {
    explainQuery := "EXPLAIN ANALYZE " + query
    rows, err := db.Query(explainQuery, args...)
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var plan string
        if err := rows.Scan(&plan); err != nil {
            return err
        }
        fmt.Println(plan)
    }

    return rows.Err()
}

// Мониторинг медленных запросов
type SlowQueryLogger struct {
    threshold time.Duration
    logger    *log.Logger
}

func (sql *SlowQueryLogger) LogQuery(query string, duration time.Duration) {
    if duration > sql.threshold {
        sql.logger.Printf("Slow query (%.2fms): %s",
            duration.Seconds()*1000, query)
    }
}
```

## Лучшие практики

1. **Используйте пулы соединений** — настраивайте **SetMaxOpenConns**, **SetMaxIdleConns**
2. **Используйте подготовленные запросы** — для повторяющихся запросов
3. **Обрабатывайте ошибки** — всегда проверяйте ошибки при работе с БД
4. **Используйте транзакции** — для атомарных операций
5. **Закрывайте ресурсы** — используйте **defer** для закрытия **rows** и **connections**
6. **Используйте context** — для отмены долгих запросов
7. **Используйте индексы** — для оптимизации запросов
8. **Избегайте N+1 проблем** — используйте **JOIN** или **Preload**
9. **Используйте batch операции** — для массовых вставок
10. **Мониторьте производительность** — отслеживайте медленные запросы
11. **Используйте `Repository` паттерн** — для абстракции доступа к данным
12. **Используйте миграции** — для управления схемой БД
13. **Тестируйте запросы** — проверяйте производительность запросов
14. **Используйте connection pooling** — правильно настраивайте пулы
15. **Используйте prepared statements** — для безопасности и производительности

### Практические примеры: Оптимизация запросов через EXPLAIN

```go
func AnalyzeQuery(db *sql.DB, query string, args ...interface{}) error {
    explainQuery := "EXPLAIN ANALYZE " + query

    rows, err := db.Query(explainQuery, args...)
    if err != nil {
        return err
    }
    defer rows.Close()

    for rows.Next() {
        var plan string
        if err := rows.Scan(&plan); err != nil {
            return err
        }
        log.Printf("Query plan: %s", plan)
    }

    return rows.Err()
}

// Использование
func OptimizeSlowQuery(db *sql.DB) error {
    query := `SELECT * FROM users WHERE email = $1`
    return AnalyzeQuery(db, query, "user@example.com")
}
```

### Практические примеры: Кэширование результатов запросов

```go
type QueryCache struct {
    cache map[string]interface{}
    mu    sync.RWMutex
    ttl   time.Duration
}

func NewQueryCache(ttl time.Duration) *QueryCache {
    return &QueryCache{
        cache: make(map[string]interface{}),
        ttl:   ttl,
    }
}

func (qc *QueryCache) Get(key string) (interface{}, bool) {
    qc.mu.RLock()
    defer qc.mu.RUnlock()
    value, ok := qc.cache[key]
    return value, ok
}

func (qc *QueryCache) Set(key string, value interface{}) {
    qc.mu.Lock()
    defer qc.mu.Unlock()
    qc.cache[key] = value

    // Автоматическое удаление через TTL
    time.AfterFunc(qc.ttl, func() {
        qc.mu.Lock()
        delete(qc.cache, key)
        qc.mu.Unlock()
    })
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

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
