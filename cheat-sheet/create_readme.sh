#!/bin/bash

# Script to create basic README.md files for empty directories

create_readme() {
    local dir="$1"
    local name=$(basename "$dir")
    local title=$(echo "$name" | tr '-' ' ' | sed 's/\b\w/\U&/g')
    
    # Create directory if it doesn't exist
    mkdir -p "$dir"
    
    # Create README.md
    cat > "$dir/README.md" << EOT
# $title

**$title** — это ${get_description "$name"}.

**Дата последнего обновления:** 2026-01-24

## Содержание

- [Введение](#введение)
- [Основные возможности](#основные-возможности)
- [Полезные ссылки](#полезные-ссылки)

## Введение

${get_intro "$name"}

## Основные возможности

${get_features "$name"}

## Полезные ссылки

${get_links "$name"}
EOT
}

get_description() {
    case "$1" in
        "timescaledb") echo "расширение PostgreSQL для работы с временными рядами";;
        "oracle") echo "мощная реляционная база данных от Oracle Corporation";;
        "sql-server") echo "реляционная база данных от Microsoft";;
        "neo4j") echo "графовая база данных";;
        "orientdb") echo "мультимодельная NoSQL база данных";;
        "orm") echo "технологии объектно-реляционного отображения";;
        "couchbase") echo "распределенная NoSQL база данных";;
        "sql") echo "структурированный язык запросов к базам данных";;
        "azure") echo "облачная платформа от Microsoft";;
        "gcp") echo "облачная платформа от Google";;
        *) echo "важная технология в области разработки";;
    esac
}

get_intro() {
    case "$1" in
        "timescaledb") echo "TimescaleDB — это расширение PostgreSQL, оптимизированное для работы с временными рядами и аналитикой больших данных.";;
        "oracle") echo "Oracle Database — это объектно-реляционная база данных, широко используемая в enterprise приложениях.";;
        "sql-server") echo "SQL Server — это реляционная база данных от Microsoft с широкими возможностями для бизнес-аналитики.";;
        "neo4j") echo "Neo4j — это нативная графовая база данных, оптимизированная для работы с сильно связанными данными.";;
        "orientdb") echo "OrientDB — это мультимодельная NoSQL база данных, поддерживающая графовую, документную и объектную модели.";;
        "orm") echo "ORM (Object-Relational Mapping) — это технология, которая позволяет работать с реляционными базами данных используя объектно-ориентированный подход.";;
        "couchbase") echo "Couchbase — это распределенная NoSQL база данных, оптимизированная для высокой производительности и масштабируемости.";;
        "sql") echo "SQL (Structured Query Language) — это стандартный язык для работы с реляционными базами данных.";;
        "azure") echo "Microsoft Azure — это облачная платформа с широким спектром сервисов для разработки и развертывания приложений.";;
        "gcp") echo "Google Cloud Platform — это набор облачных сервисов от Google для вычислений, хранения и анализа данных.";;
        *) echo "Это важная технология в современной разработке программного обеспечения.";;
    esac
}

get_features() {
    case "$1" in
        "timescaledb") echo "- **Гипертаблицы** — автоматическое партиционирование по времени
- **Оптимизированные запросы** — специализированные функции для временных данных
- **Сжатие данных** — эффективное хранение больших объемов данных
- **PostgreSQL совместимость** — все возможности PostgreSQL плюс расширения";;
        "oracle") echo "- **Высокая надежность** — ACID транзакции и disaster recovery
- **Масштабируемость** — поддержка кластеров и распределенных систем
- **Безопасность** — продвинутые механизмы аутентификации и авторизации
- **PL/SQL** — процедурный язык для stored procedures";;
        "sql-server") echo "- **Integration Services** — ETL возможности
- **Analysis Services** — OLAP кубы и бизнес-аналитика
- **Reporting Services** — генерация отчетов
- **T-SQL** — расширенный диалект SQL";;
        "neo4j") echo "- **Cypher** — декларативный язык запросов к графам
- **ACID транзакции** — гарантии целостности данных
- **Высокая производительность** — оптимизированные графовые алгоритмы
- **Скалярность** — поддержка кластеров";;
        "orientdb") echo "- **Множественные модели** — документная, графовая, объектная
- **SQL-подобный язык** — поддержка SQL для графовых запросов
- **ACID транзакции** — гарантии целостности
- **Горизонтальное масштабирование** — поддержка кластеров";;
        "orm") echo "- **Объектно-реляционное отображение** — автоматическое преобразование объектов в SQL
- **Ленивая загрузка** — оптимизация запросов к базе данных
- **Кэширование** — улучшение производительности
- **Миграции** — управление схемой базы данных";;
        "couchbase") echo "- **N1QL** — SQL-подобный язык для JSON документов
- **Кластеризация** — автоматическое шардирование и репликация
- **Высокая производительность** — оптимизированная архитектура
- **Мобильная синхронизация** — поддержка мобильных приложений";;
        "sql") echo "- **DDL** — язык определения данных (CREATE, ALTER, DROP)
- **DML** — язык манипуляции данными (SELECT, INSERT, UPDATE, DELETE)
- **DCL** — язык управления доступом (GRANT, REVOKE)
- **TCL** — язык управления транзакциями (COMMIT, ROLLBACK)";;
        "azure") echo "- **Compute** — виртуальные машины, контейнеры, serverless
- **Storage** — объектное хранилище, базы данных, файлы
- **AI/ML** — сервисы машинного обучения и ИИ
- **DevOps** — инструменты для CI/CD и мониторинга";;
        "gcp") echo "- **Compute Engine** — виртуальные машины
- **BigQuery** — аналитика больших данных
- **AI Platform** — машинное обучение
- **Kubernetes Engine** — управляемый Kubernetes";;
        *) echo "- Широкие возможности для разработки
- Высокая производительность
- Масштабируемость
- Интеграция с другими технологиями";;
    esac
}

get_links() {
    case "$1" in
        "timescaledb") echo "- [Официальная документация](https://docs.timescale.com/)
- [GitHub репозиторий](https://github.com/timescale/timescaledb)";;
        "oracle") echo "- [Официальная документация](https://docs.oracle.com/en/database/)
- [Oracle Learning](https://learn.oracle.com/)";;
        "sql-server") echo "- [Официальная документация](https://docs.microsoft.com/en-us/sql/)
- [SQL Server Tutorials](https://docs.microsoft.com/en-us/sql/sql-server/tutorials/)";;
        "neo4j") echo "- [Официальная документация](https://neo4j.com/docs/)
- [Neo4j Developer](https://neo4j.com/developer/)";;
        "orientdb") echo "- [Официальная документация](https://orientdb.org/docs/)
- [GitHub репозиторий](https://github.com/orientechnologies/orientdb)";;
        "orm") echo "- [Hibernate](https://hibernate.org/) — популярная Java ORM
- [Entity Framework](https://docs.microsoft.com/en-us/ef/) — .NET ORM
- [SQLAlchemy](https://www.sqlalchemy.org/) — Python ORM";;
        "couchbase") echo "- [Официальная документация](https://docs.couchbase.com/)
- [Couchbase Developer](https://developer.couchbase.com/)";;
        "sql") echo "- [SQL Tutorial](https://www.w3schools.com/sql/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MySQL Reference](https://dev.mysql.com/doc/)";;
        "azure") echo "- [Документация Azure](https://docs.microsoft.com/en-us/azure/)
- [Microsoft Learn](https://docs.microsoft.com/en-us/learn/)";;
        "gcp") echo "- [Google Cloud Documentation](https://cloud.google.com/docs)
- [Google Cloud Training](https://cloud.google.com/training)";;
        *) echo "- Официальная документация
- Документация сообщества";;
    esac
}

# Get empty directories and create README files
find cheatsheets -type d -empty | while read -r dir; do
    echo "Creating README for: $dir"
    create_readme "$dir"
done

echo "Done creating README files"
