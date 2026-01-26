# Реляционные базы данных (Relational Databases)

**ACID транзакции, SQL, реляционная модель данных**

## 📋 Описание

Этот раздел содержит детальные руководства по реляционным базам данных: PostgreSQL, MySQL, Oracle, SQL Server. Каждая БД покрыта от основ до продвинутых тем: проектирование схемы, оптимизация запросов, индексы, транзакции, партиционирование.

## 📚 Структура раздела

### 🐘 [PostgreSQL](postgresql/)
**21 файл** - Продвинутая реляционная БД

- **PostgreSQL Basics** - Основы PostgreSQL
- **PostgreSQL Design** - Проектирование схемы
- **PostgreSQL Queries** - SQL запросы
- **PostgreSQL Indexes** - Индексы и оптимизация
- **PostgreSQL Transactions** - Транзакции и изоляция
- **PostgreSQL Partitioning** - Партиционирование

### 🐬 [MySQL](mysql/)
**7 файлов** - Популярная реляционная БД

- **[MySQL Basics](mysql/mysql-basics.md)** - Основы MySQL
- **[MySQL Queries](mysql/mysql-queries.md)** - SQL запросы

### 🏛️ [Other Relational](.)
**Другие реляционные БД**

- **[Oracle](oracle/oracle-basics.md)** - Oracle Database
- **[SQL Server](sql-server/sql-server-basics.md)** - Microsoft SQL Server

## 🎯 Для кого этот раздел

### Database Developers
- **Проектирование схемы** - нормализация, индексы
- **Оптимизация запросов** - производительность
- **Транзакции** - ACID, изоляция

### Backend Developers
- **Работа с БД** - SQL, ORM
- **Выбор БД** - PostgreSQL vs MySQL vs Oracle
- **Best practices** - лучшие практики

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. SQL Basics
   ├── SELECT, INSERT, UPDATE, DELETE
   ├── JOIN операции
   └── Индексы

2. PostgreSQL
   ├── PostgreSQL Basics
   ├── Проектирование схемы
   └── Оптимизация запросов

3. Transactions
   └── ACID свойства
```

### Для опытных
```
1. Advanced PostgreSQL
   ├── Партиционирование
   ├── Репликация
   └── Шардинг

2. Performance Tuning
   ├── Query Optimization
   ├── Index Strategy
   └── Connection Pooling

3. High Availability
   ├── Replication
   ├── Failover
   └── Backup & Recovery
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../../frameworks/)** - Spring Data JPA, Hibernate
- **[Interview](../../interview/databases/)** - Вопросы на собеседовании
- **[ORM](../../orm/)** - Object-Relational Mapping
- **[SQL](../../sql/)** - SQL основы

### Специфичные связи
- **PostgreSQL** → [Spring Data JPA](../../frameworks/java-frameworks/spring/spring-data-jpa.md)
- **MySQL** → [Spring Data JPA](../../frameworks/java-frameworks/spring/spring-data-jpa.md)
- **SQL** → [Interview](../../interview/databases/sql-interview.md)

## 📚 Полезные ресурсы

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Oracle Documentation](https://docs.oracle.com/en/database/)

### Учебные материалы
- [PostgreSQL Tutorial](https://www.postgresqltutorial.com/)
- [MySQL Tutorial](https://dev.mysql.com/doc/refman/8.0/en/tutorial.html)

## 🎯 Следующие шаги

После изучения реляционных БД:
1. **Изучите ORM** → [ORM](../../orm/)
2. **Освойте Spring Data JPA** → [Spring Data JPA](../../frameworks/java-frameworks/spring/spring-data-jpa.md)
3. **Подготовьтесь к интервью** → [Interview](../../interview/databases/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../nosql/)

*Обновлено: 2026-01-25*
