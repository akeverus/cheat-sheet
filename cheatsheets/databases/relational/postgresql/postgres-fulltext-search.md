---
title: "PostgreSQL: Полнотекстовый поиск"
description: "Полное руководство по полнотекстовому поиску в PostgreSQL: tsvector, tsquery, GIN индексы, конфигурации языков, ранжирование результатов, поиск по нескольким полям"
tags: ["postgresql", "fulltext-search", "tsvector", "tsquery", "gin-index", "search"]
difficulty: "intermediate"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-indexes.md"]
next: ["databases/postgres-extensions.md", "databases/postgres-performance-tuning.md"]
updated: "2026-02-06"
related: ["databases/postgres-queries.md", "databases/postgres-indexes.md"]
---

# **PostgreSQL**: Полнотекстовый поиск

**Дата последнего обновления:** 2026-02-03

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Full Text Search](https://www.postgresql.org/docs/current/textsearch.html) — полнотекстовый поиск

### См. также
- [postgres-basics.md](postgres-basics.md) — основы PostgreSQL
- [postgres-queries.md](postgres-queries.md) — запросы

## Содержание

- [**PostgreSQL**: Полнотекстовый поиск](#postgresql-полнотекстовый-поиск)
- [Введение в полнотекстовый поиск](#введение-в-полнотекстовый-поиск)
  - [Основные компоненты](#основные-компоненты)
- [**tsvector**](#tsvector)
  - [Создание **tsvector**](#создание-tsvector)
  - [Обработка текста](#обработка-текста)
  - [Операции с **tsvector**](#операции-с-tsvector)
- [**tsquery**](#tsquery)
  - [Создание **tsquery**](#создание-tsquery)
  - [Операторы **tsquery**](#операторы-tsquery)
  - [Примеры запросов](#примеры-запросов)
- [Базовый полнотекстовый поиск](#базовый-полнотекстовый-поиск)
  - [Простой поиск](#простой-поиск)
  - [Создание индекса для поиска](#создание-индекса-для-поиска)
  - [Использование индекса](#использование-индекса)
- [Ранжирование результатов](#ранжирование-результатов)
  - [**ts_rank**](#ts_rank)
  - [**ts_rank_cd**](#ts_rank_cd)
  - [Настройка весов](#настройка-весов)
  - [Веса по умолчанию](#веса-по-умолчанию)
- [Поиск по нескольким полям](#поиск-по-нескольким-полям)
  - [Объединение полей](#объединение-полей)
  - [Создание функции для поиска](#создание-функции-для-поиска)
- [Конфигурации языков](#конфигурации-языков)
  - [Доступные конфигурации](#доступные-конфигурации)
  - [Создание собственной конфигурации](#создание-собственной-конфигурации)
  - [Настройка стоп-слов](#настройка-стоп-слов)
- [**GIN** индексы для полнотекстового поиска](#gin-индексы-для-полнотекстового-поиска)
  - [Создание **GIN** индекса](#создание-gin-индекса)
  - [Оптимизация **GIN** индексов](#оптимизация-gin-индексов)
  - [Мониторинг **GIN** индексов](#мониторинг-gin-индексов)
- [Продвинутые техники](https://www.postgresql.org/docs/)
  - [Поиск с автодополнением](#поиск-с-автодополнением)
  - [Поиск с учетом морфологии](#поиск-с-учетом-морфологии)
  - [Поиск с подсветкой результатов](#поиск-с-подсветкой-результатов)
- [**Best Practices**](#лучшие-практики)
  - [Производительность](#производительность)
  - [Качество поиска](#качество-поиска)

  - [Проблема: Медленный поиск](#проблема-медленный-поиск)
  - [Проблема: Неточные результаты](#проблема-неточные-результаты)
- [**Advanced Full-Text Search**](#advanced-full-text-search)
  - [Кэширование **tsvector**](#кэширование-tsvector)
  - [Многоязычный поиск](#многоязычный-поиск)
  - [Поиск с фильтрами](#поиск-с-фильтрами)
  - [Поиск с синонимами](#поиск-с-синонимами)
- [**Performance Optimization**](#performance-optimization)
  - [Оптимизация индексов](#оптимизация-индексов)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Мониторинг производительности](#мониторинг-производительности)
- [**Integration with Other Extensions**](#integration-with-other-extensions)
  - [Интеграция с **pg_trgm**](#интеграция-с-pg_trgm)
  - [Интеграция с **PostGIS**](#интеграция-с-postgis)
- [**Real-World Examples**](#real-world-examples)
  - [Пример 1: Поисковая система для блога](#пример-1-поисковая-система-для-блога)
  - [Пример 2: Поиск в документах](#пример-2-поиск-в-документах)
  - [Безопасность](#безопасность)
- [**Advanced Techniques**](#advanced-techniques)
  - [Поиск с фасетами](#поиск-с-фасетами)
  - [Поиск с релевантностью по времени](#поиск-с-релевантностью-по-времени)
  - [Поиск с кластеризацией](#поиск-с-кластеризацией)
- [**Custom Text Search Configurations**](#custom-text-search-configurations)
  - [Создание конфигурации для технических терминов](https://www.postgresql.org/docs/)
  - [Создание конфигурации для многоязычного контента](#создание-конфигурации-для-многоязычного-контента)
- [**Text Search Dictionaries**](#text-search-dictionaries)
  - [Создание собственного словаря](#создание-собственного-словаря)
  - [Использование **thesaurus**](#использование-thesaurus)
- [**Performance Tuning**](#performance-tuning)
- [**Monitoring and Maintenance**](#monitoring-and-maintenance)
  - [Обслуживание индексов](#обслуживание-индексов)
- [Решение проблем](#решение-проблем)
  - [Проблема: Индекс не используется](#проблема-индекс-не-используется)
  - [Проблема: Медленное обновление индекса](#проблема-медленное-обновление-индекса)
  - [Проблема: Большой размер индекса](#проблема-большой-размер-индекса)
- [**Integration Examples**](#integration-examples)
  - [Интеграция с веб-приложением](#интеграция-с-веб-приложением)

## Введение в полнотекстовый поиск

**PostgreSQL** предоставляет мощные встроенные возможности полнотекстового поиска, которые позволяют эффективно искать текст в больших объемах данных.

### Основные компоненты

1. **tsvector**: Векторное представление текста
2. **tsquery**: Запрос для поиска
3. **GIN индексы**: Индексы для быстрого поиска
4. **Ранжирование**: Сортировка результатов по релевантности

---

## **tsvector**

`**tsvector**` - это тип данных, представляющий документ в виде отсортированного списка лексем (**слов**).

### Создание **tsvector**

**Примеры создания **tsvector** и использования **to_tsvector**:**

```sql
-- Простое создание tsvector
SELECT 'PostgreSQL is a powerful database'::tsvector;

-- Результат: 'PostgreSQL' 'a' 'database' 'is' 'powerful'

-- Использование to_tsvector
SELECT to_tsvector('english', 'PostgreSQL is a powerful database');

-- Результат: 'databas':4 'power':3 'postgresql':1
```

### Обработка текста

```sql
-- to_tsvector с указанием языка
SELECT to_tsvector('english', 'The quick brown fox jumps over the lazy dog');

-- to_tsvector без указания языка (использует default_text_search_config)
SELECT to_tsvector('The quick brown fox jumps over the lazy dog');

-- Обработка нескольких полей
SELECT to_tsvector('english', 'title: ' || title || ' body: ' || body)
FROM articles;
```

### Операции с **tsvector**

```sql
-- Объединение tsvector
SELECT to_tsvector('english', 'PostgreSQL') || to_tsvector('english', 'database');

-- Удаление стоп-слов
SELECT to_tsvector('english', 'the quick brown fox');

-- Нормализация
SELECT to_tsvector('english', 'PostgreSQL') = to_tsvector('english', 'postgresql');
```

---

## **tsquery**

`**tsquery**` - это тип данных для представления поисковых запросов.

### Создание **tsquery**

```sql
-- Простое создание tsquery
SELECT 'PostgreSQL & database'::tsquery;

-- Использование to_tsquery
SELECT to_tsquery('english', 'PostgreSQL & database');

-- Поиск с OR
SELECT to_tsquery('english', 'PostgreSQL | database');

-- Поиск с NOT
SELECT to_tsquery('english', 'PostgreSQL & !database');

-- Поиск с фразами
SELECT to_tsquery('english', 'PostgreSQL <-> database');
```

### Операторы **tsquery**

- `&` (**AND**): Оба термина должны присутствовать
- `|` (**OR**): Хотя бы один термин должен присутствовать
- `!` (**NOT**): Термин не должен присутствовать
- `<->` (**FOLLOWED BY**): Термины должны следовать друг за другом
- `<N>` (**FOLLOWED `BY` N**): Термины должны быть на расстоянии N слов

### Примеры запросов

```sql
-- Поиск с AND
SELECT to_tsquery('english', 'PostgreSQL & database');

-- Поиск с OR
SELECT to_tsquery('english', 'PostgreSQL | MySQL');

-- Поиск с NOT
SELECT to_tsquery('english', 'database & !MySQL');

-- Поиск фразы
SELECT to_tsquery('english', 'PostgreSQL <-> database');

-- Поиск с расстоянием
SELECT to_tsquery('english', 'PostgreSQL <2> database');
```

---

## Базовый полнотекстовый поиск

### Простой поиск

```sql
-- Поиск в тексте
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL');

-- Поиск в нескольких полях
SELECT title, body
FROM articles
WHERE to_tsvector('english', title || ' ' || body) @@ to_tsquery('english', 'PostgreSQL');
```

### Создание индекса для поиска

```sql
-- Создать GIN индекс
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(to_tsvector('english', body));

-- Создать индекс для нескольких полей
CREATE INDEX idx_articles_search_gin ON articles 
USING gin(to_tsvector('english', title || ' ' || body));
```

### Использование индекса

```sql
-- Поиск с использованием индекса
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL');

-- EXPLAIN для проверки использования индекса
EXPLAIN ANALYZE
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL');
```

---

## Ранжирование результатов

### **ts_rank**

`**ts_rank**` вычисляет релевантность документа на основе частоты терминов.

```sql
-- Базовое ранжирование
SELECT 
    title,
    body,
    ts_rank(to_tsvector('english', body), to_tsquery('english', 'PostgreSQL')) AS rank
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL')
ORDER BY rank DESC;
```

### **ts_rank_cd**

`**ts_rank_cd**` использует алгоритм покрытия плотности для более точного ранжирования.

```sql
-- Ранжирование с покрытием плотности
SELECT 
    title,
    body,
    ts_rank_cd(to_tsvector('english', body), to_tsquery('english', 'PostgreSQL')) AS rank
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL')
ORDER BY rank DESC;
```

### Настройка весов

```sql
-- Ранжирование с весами
SELECT 
    title,
    body,
    ts_rank(
        '{0.1, 0.2, 0.4, 1.0}',
        to_tsvector('english', title || ' ' || body),
        to_tsquery('english', 'PostgreSQL')
    ) AS rank
FROM articles
WHERE to_tsvector('english', title || ' ' || body) @@ to_tsquery('english', 'PostgreSQL')
ORDER BY rank DESC;
```

### Веса по умолчанию

- `D`: заголовок документа
- `C`: заголовок раздела
- `B`: тело документа
- `A`: аннотация

---

## Поиск по нескольким полям

### Объединение полей

```sql
-- Поиск в нескольких полях с разными весами
SELECT 
    title,
    body,
    ts_rank(
        '{0.1, 0.2, 0.4, 1.0}',
        setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(body, '')), 'B'),
        to_tsquery('english', 'PostgreSQL')
    ) AS rank
FROM articles
WHERE 
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(body, '')), 'B')
    @@ to_tsquery('english', 'PostgreSQL')
ORDER BY rank DESC;
```

### Создание функции для поиска

```sql
-- Создать функцию для полнотекстового поиска
CREATE OR REPLACE FUNCTION articles_search(search_query text)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(
            setweight(to_tsvector('english', coalesce(a.title, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(a.body, '')), 'B'),
            to_tsquery('english', search_query)
        ) AS rank
    FROM articles a
    WHERE 
        setweight(to_tsvector('english', coalesce(a.title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(a.body, '')), 'B')
        @@ to_tsquery('english', search_query)
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;

-- Использование функции
SELECT * FROM articles_search('PostgreSQL & database');
```

---

## Конфигурации языков

### Доступные конфигурации

```sql
-- Просмотр доступных конфигураций
SELECT cfgname FROM pg_ts_config;

-- Установка конфигурации по умолчанию
SET default_text_search_config = 'english';

-- Использование конкретной конфигурации
SELECT to_tsvector('russian', 'PostgreSQL - это мощная база данных');
```

### Создание собственной конфигурации

```sql
-- Создать конфигурацию на основе существующей
CREATE TEXT SEARCH CONFIGURATION my_config (COPY = english);

-- Добавить словарь
ALTER TEXT SEARCH CONFIGURATION my_config
ALTER MAPPING FOR asciiword, word
WITH unaccent, english_stem;
```

### Настройка стоп-слов

```sql
-- Просмотр стоп-слов
SELECT * FROM pg_get_keywords();

-- Создать собственный список стоп-слов
CREATE TEXT SEARCH DICTIONARY my_stopwords (
    TEMPLATE = pg_catalog.simple,
    STOPWORDS = english
);
```

---

## **GIN** индексы для полнотекстового поиска

### Создание **GIN** индекса

```sql
-- Базовый GIN индекс
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(to_tsvector('english', body));

-- GIN индекс для нескольких полей
CREATE INDEX idx_articles_search_gin ON articles 
USING gin(
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(body, '')), 'B')
);
```

### Оптимизация **GIN** индексов

```sql
-- Настройка параметров GIN индекса
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(to_tsvector('english', body))
WITH (fastupdate = off);

-- Перестроить индекс
REINDEX INDEX idx_articles_body_gin;
```

### Мониторинг **GIN** индексов

```sql
-- Размер индекса
SELECT 
    pg_size_pretty(pg_relation_size('idx_articles_body_gin')) AS index_size;

-- Статистика использования индекса
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE indexname = 'idx_articles_body_gin';
```

---

## Продвинутые техники

### Поиск с автодополнением

```sql
-- Поиск с префиксом
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'Postgre:*');

-- Поиск с суффиксом (требует дополнительной настройки)
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', ':*SQL');
```

### Поиск с учетом морфологии

```sql
-- Поиск с учетом различных форм слова
SELECT title, body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'databases');

-- Найдет: database, databases, database's
```

### Поиск с подсветкой результатов

```sql
-- Подсветка найденных терминов
SELECT 
    title,
    ts_headline(
        'english',
        body,
        to_tsquery('english', 'PostgreSQL'),
        'StartSel=<mark>, StopSel=</mark>, MaxWords=35, MinWords=15'
    ) AS highlighted_body
FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL');
```

---

## Лучшие практики

### Производительность

1. **Всегда создавайте `GIN` индексы** для полей с полнотекстовым поиском
2. **Используйте ранжирование** для сортировки результатов
3. **Ограничивайте результаты** с помощью **LIMIT**
4. **Кэшируйте tsvector** в отдельной колонке для больших документов

### Качество поиска

1. **Выбирайте правильную конфигурацию языка**
2. **Настраивайте веса** для разных полей
3. **Тестируйте запросы** на реальных данных
4. **Мониторьте производительность** поиска

### Проблема: Медленный поиск

**Решение:**
```sql
-- Создать GIN индекс
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(to_tsvector('english', body));

-- Проверить использование индекса
EXPLAIN ANALYZE
SELECT * FROM articles
WHERE to_tsvector('english', body) @@ to_tsquery('english', 'PostgreSQL');
```

### Проблема: Неточные результаты

**Решение:**
```sql
-- Использовать ts_rank_cd вместо ts_rank
-- Настроить веса для разных полей
-- Использовать правильную конфигурацию языка
```

## **Advanced Full-Text Search**

### Кэширование **tsvector**

Для больших документов рекомендуется кэшировать **tsvector** в отдельной колонке.

```sql
-- Добавить колонку для кэширования tsvector
ALTER TABLE articles ADD COLUMN body_tsvector tsvector;

-- Создать индекс на кэшированном tsvector
CREATE INDEX idx_articles_body_tsvector_gin ON articles USING gin(body_tsvector);

-- Обновить tsvector при изменении данных
CREATE OR REPLACE FUNCTION update_articles_tsvector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.body_tsvector := to_tsvector('english', coalesce(NEW.body, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER articles_tsvector_update
BEFORE INSERT OR UPDATE ON articles
FOR EACH ROW EXECUTE FUNCTION update_articles_tsvector();

-- Обновить существующие записи
UPDATE articles SET body_tsvector = to_tsvector('english', coalesce(body, ''));

-- Использовать кэшированный tsvector в запросах
SELECT title, body
FROM articles
WHERE body_tsvector @@ to_tsquery('english', 'PostgreSQL');
```

### Многоязычный поиск

```sql
-- Создать функцию для многоязычного поиска
CREATE OR REPLACE FUNCTION multilingual_search(
    search_text TEXT,
    languages TEXT[] DEFAULT ARRAY['english', 'russian']
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL
) AS $$
DECLARE
    lang TEXT;
    query tsquery := '';
BEGIN
    FOREACH lang IN ARRAY languages
    LOOP
        IF query = '' THEN
            query := to_tsquery(lang, search_text);
        ELSE
            query := query || to_tsquery(lang, search_text);
        END IF;
    END LOOP;
    
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(
            to_tsvector('english', coalesce(a.title, '') || ' ' || coalesce(a.body, '')),
            query
        ) AS rank
    FROM articles a
    WHERE 
        to_tsvector('english', coalesce(a.title, '') || ' ' || coalesce(a.body, '')) @@ query
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM multilingual_search('PostgreSQL', ARRAY['english', 'russian']);
```

### Поиск с фильтрами

```sql
-- Поиск с дополнительными фильтрами
CREATE OR REPLACE FUNCTION articles_search_filtered(
    search_query TEXT,
    category_id INTEGER DEFAULT NULL,
    author_id INTEGER DEFAULT NULL,
    date_from DATE DEFAULT NULL,
    date_to DATE DEFAULT NULL
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(
            setweight(to_tsvector('english', coalesce(a.title, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(a.body, '')), 'B'),
            to_tsquery('english', search_query)
        ) AS rank
    FROM articles a
    WHERE 
        setweight(to_tsvector('english', coalesce(a.title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(a.body, '')), 'B')
        @@ to_tsquery('english', search_query)
        AND (category_id IS NULL OR a.category_id = category_id)
        AND (author_id IS NULL OR a.author_id = author_id)
        AND (date_from IS NULL OR a.created_at >= date_from)
        AND (date_to IS NULL OR a.created_at <= date_to)
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;
```

### Поиск с автодополнением

```sql
-- Создать функцию для автодополнения
CREATE OR REPLACE FUNCTION search_autocomplete(
    prefix TEXT,
    limit_count INTEGER DEFAULT 10
)
RETURNS TABLE(term TEXT, count BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        word AS term,
        ndoc AS count
    FROM ts_stat($$
        SELECT body_tsvector FROM articles
        WHERE body_tsvector @@ to_tsquery('english', prefix || ':*')
    $$)
    WHERE word LIKE prefix || '%'
    ORDER BY count DESC, word
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM search_autocomplete('Postgre', 10);
```

### Поиск с синонимами

```sql
-- Создать таблицу синонимов
CREATE TABLE search_synonyms (
    id SERIAL PRIMARY KEY,
    term TEXT NOT NULL,
    synonyms TEXT[] NOT NULL
);

INSERT INTO search_synonyms (term, synonyms) VALUES
('PostgreSQL', ARRAY['Postgres', 'PG']),
('database', ARRAY['DB', 'datastore']);

-- Функция для расширения запроса синонимами
CREATE OR REPLACE FUNCTION expand_query_with_synonyms(query_text TEXT)
RETURNS tsquery AS $$
DECLARE
    expanded_query tsquery;
    synonym_rec RECORD;
    term TEXT;
BEGIN
    expanded_query := to_tsquery('english', query_text);
    
    FOR synonym_rec IN
        SELECT term, synonyms FROM search_synonyms
    LOOP
        IF to_tsquery('english', synonym_rec.term) @@ expanded_query THEN
            expanded_query := expanded_query | to_tsquery('english', 
                array_to_string(synonym_rec.synonyms, ' | '));
        END IF;
    END LOOP;
    
    RETURN expanded_query;
END;
$$ LANGUAGE plpgsql;

-- Использовать расширенный запрос
SELECT title, body
FROM articles
WHERE body_tsvector @@ expand_query_with_synonyms('PostgreSQL');
```

## **Performance Optimization**

### Оптимизация индексов

```sql
-- Создать частичный индекс для активных статей
CREATE INDEX idx_articles_active_gin ON articles 
USING gin(to_tsvector('english', body))
WHERE status = 'active';

-- Создать индекс с настройками производительности
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(to_tsvector('english', body))
WITH (
    fastupdate = off,
    gin_pending_list_limit = 1000
);

-- Перестроить индекс для оптимизации
REINDEX INDEX CONCURRENTLY idx_articles_body_gin;
```

### Оптимизация запросов

```sql
-- Использовать кэшированный tsvector
SELECT title, body
FROM articles
WHERE body_tsvector @@ to_tsquery('english', 'PostgreSQL')
ORDER BY ts_rank(body_tsvector, to_tsquery('english', 'PostgreSQL')) DESC
LIMIT 20;

-- Использовать prepared statements
PREPARE search_articles(TEXT) AS
SELECT title, body
FROM articles
WHERE body_tsvector @@ to_tsquery('english', $1)
ORDER BY ts_rank(body_tsvector, to_tsquery('english', $1)) DESC
LIMIT 20;

EXECUTE search_articles('PostgreSQL');
```

### Мониторинг производительности

```sql
-- Проверить использование индексов
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE indexname LIKE '%gin%'
ORDER BY idx_scan DESC;

-- Проверить размер индексов
SELECT 
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE indexname LIKE '%gin%'
ORDER BY pg_relation_size(indexrelid) DESC;
```

## **Integration with Other Extensions**

### Интеграция с **pg_trgm**

```sql
-- Комбинировать полнотекстовый поиск с триграммами
CREATE OR REPLACE FUNCTION hybrid_search(
    search_text TEXT
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    fts_rank REAL,
    similarity REAL,
    combined_rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(body_tsvector, to_tsquery('english', search_text)) AS fts_rank,
        similarity(a.title, search_text) AS similarity,
        (
            ts_rank(body_tsvector, to_tsquery('english', search_text)) * 0.7 +
            similarity(a.title, search_text) * 0.3
        ) AS combined_rank
    FROM articles a
    WHERE 
        body_tsvector @@ to_tsquery('english', search_text)
        OR a.title % search_text
    ORDER BY combined_rank DESC;
END;
$$ LANGUAGE plpgsql;
```

### Интеграция с **PostGIS**

```sql
-- Поиск с учетом геолокации
CREATE OR REPLACE FUNCTION geo_search(
    search_text TEXT,
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    radius_meters INTEGER DEFAULT 1000
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL,
    distance_meters DOUBLE PRECISION
) AS $$
DECLARE
    point GEOGRAPHY;
BEGIN
    point := ST_GeogFromText(format('POINT(%s %s)', lon, lat), 4326);
    
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(body_tsvector, to_tsquery('english', search_text)) AS rank,
        ST_Distance(a.location, point) AS distance
    FROM articles a
    WHERE 
        body_tsvector @@ to_tsquery('english', search_text)
        AND ST_DWithin(a.location, point, radius_meters)
    ORDER BY rank DESC, distance;
END;
$$ LANGUAGE plpgsql;
```

## **Real-World Examples**

### Пример 1: Поисковая система для блога

```sql
-- Создать таблицу статей
CREATE TABLE blog_posts (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    author_id INTEGER,
    category_id INTEGER,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    search_vector tsvector
);

-- Создать индекс
CREATE INDEX idx_blog_posts_search ON blog_posts USING gin(search_vector);

-- Триггер для обновления search_vector
CREATE OR REPLACE FUNCTION update_blog_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector := 
        setweight(to_tsvector('english', coalesce(NEW.title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(NEW.content, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER blog_posts_search_vector_update
BEFORE INSERT OR UPDATE ON blog_posts
FOR EACH ROW EXECUTE FUNCTION update_blog_search_vector();

-- Функция поиска
CREATE OR REPLACE FUNCTION search_blog_posts(
    query_text TEXT,
    category_filter INTEGER DEFAULT NULL,
    limit_count INTEGER DEFAULT 20
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    content TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.title,
        p.content,
        ts_rank(p.search_vector, to_tsquery('english', query_text)) AS rank
    FROM blog_posts p
    WHERE 
        p.search_vector @@ to_tsquery('english', query_text)
        AND (category_filter IS NULL OR p.category_id = category_filter)
    ORDER BY rank DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;
```

### Пример 2: Поиск в документах

```sql
-- Создать таблицу документов
CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    filename TEXT NOT NULL,
    content TEXT NOT NULL,
    metadata JSONB,
    search_vector tsvector,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Индекс для поиска
CREATE INDEX idx_documents_search ON documents USING gin(search_vector);
CREATE INDEX idx_documents_metadata ON documents USING gin(metadata);

-- Функция поиска с фильтрами по метаданным
CREATE OR REPLACE FUNCTION search_documents(
    query_text TEXT,
    metadata_filter JSONB DEFAULT NULL
)
RETURNS TABLE(
    id INTEGER,
    filename TEXT,
    content TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        d.id,
        d.filename,
        d.content,
        ts_rank(d.search_vector, to_tsquery('english', query_text)) AS rank
    FROM documents d
    WHERE 
        d.search_vector @@ to_tsquery('english', query_text)
        AND (metadata_filter IS NULL OR d.metadata @> metadata_filter)
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;
```

## **Best Practices Summary**

### Производительность

1. **Кэшируйте tsvector** в отдельной колонке для больших документов
2. **Создавайте `GIN` индексы** для всех полей с полнотекстовым поиском
3. **Используйте частичные индексы** для фильтрованных данных
4. **Ограничивайте результаты** с помощью **LIMIT**
5. **Мониторьте производительность** индексов

### Качество поиска

1. **Выбирайте правильную конфигурацию языка**
2. **Настраивайте веса** для разных полей
3. **Используйте ts_rank_cd** для более точного ранжирования
4. **Тестируйте запросы** на реальных данных
5. **Настраивайте стоп-слова** для вашего домена

### Безопасность

1. **Валидируйте пользовательский ввод** перед созданием **tsquery**
2. **Используйте prepared statements** для предотвращения **SQL injection**
3. **Ограничивайте права доступа** к функциям поиска

## **Advanced Techniques**

### Поиск с фасетами

```sql
-- Создать функцию для фасетного поиска
CREATE OR REPLACE FUNCTION search_with_facets(
    query_text TEXT
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL,
    facets JSONB
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        ts_rank(body_tsvector, to_tsquery('english', query_text)) AS rank,
        jsonb_build_object(
            'category', c.name,
            'author', u.name,
            'tags', a.tags
        ) AS facets
    FROM articles a
    LEFT JOIN categories c ON a.category_id = c.id
    LEFT JOIN users u ON a.author_id = u.id
    WHERE body_tsvector @@ to_tsquery('english', query_text)
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;
```

### Поиск с релевантностью по времени

```sql
-- Поиск с учетом времени создания
CREATE OR REPLACE FUNCTION time_weighted_search(
    query_text TEXT,
    days_old INTEGER DEFAULT 30
)
RETURNS TABLE(
    id INTEGER,
    title TEXT,
    body TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.id,
        a.title,
        a.body,
        (
            ts_rank(body_tsvector, to_tsquery('english', query_text)) *
            (1.0 - EXTRACT(EPOCH FROM (NOW() - a.created_at)) / (days_old * 86400.0))
        ) AS rank
    FROM articles a
    WHERE 
        body_tsvector @@ to_tsquery('english', query_text)
        AND a.created_at > NOW() - (days_old || ' days')::INTERVAL
    ORDER BY rank DESC;
END;
$$ LANGUAGE plpgsql;
```

### Поиск с кластеризацией

```sql
-- Кластеризация результатов поиска
CREATE OR REPLACE FUNCTION clustered_search(
    query_text TEXT,
    cluster_field TEXT DEFAULT 'category_id'
)
RETURNS TABLE(
    cluster_value TEXT,
    id INTEGER,
    title TEXT,
    rank REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.category_id::TEXT AS cluster_value,
        a.id,
        a.title,
        ts_rank(body_tsvector, to_tsquery('english', query_text)) AS rank
    FROM articles a
    WHERE body_tsvector @@ to_tsquery('english', query_text)
    ORDER BY cluster_value, rank DESC;
END;
$$ LANGUAGE plpgsql;
```

## **Custom Text Search Configurations**

### Создание конфигурации для технических терминов

```sql
-- Создать конфигурацию для технических документов
CREATE TEXT SEARCH CONFIGURATION technical (COPY = english);

-- Добавить словарь для технических терминов
CREATE TEXT SEARCH DICTIONARY technical_terms (
    TEMPLATE = pg_catalog.simple,
    STOPWORDS = english
);

-- Настроить маппинг
ALTER TEXT SEARCH CONFIGURATION technical
ALTER MAPPING FOR asciiword, word
WITH technical_terms, english_stem;
```

### Создание конфигурации для многоязычного контента

```sql
-- Создать конфигурацию для смешанного контента
CREATE TEXT SEARCH CONFIGURATION multilingual (COPY = english);

-- Добавить поддержку нескольких языков
ALTER TEXT SEARCH CONFIGURATION multilingual
ALTER MAPPING FOR asciiword, word
WITH unaccent, english_stem, russian_stem;
```

## **Text Search Dictionaries**

### Создание собственного словаря

```sql
-- Создать словарь на основе simple template
CREATE TEXT SEARCH DICTIONARY my_dictionary (
    TEMPLATE = pg_catalog.simple,
    STOPWORDS = english
);

-- Создать словарь на основе synonym template
CREATE TEXT SEARCH DICTIONARY my_synonyms (
    TEMPLATE = synonym,
    SYNONYMS = my_synonyms
);

-- Файл my_synonyms.syn
-- PostgreSQL postgres
-- database db
```

### Использование **thesaurus**

```sql
-- Создать thesaurus словарь
CREATE TEXT SEARCH DICTIONARY my_thesaurus (
    TEMPLATE = thesaurus,
    DICTFILE = my_thesaurus,
    DICTIONARY = english_stem
);

-- Файл my_thesaurus.ths
-- PostgreSQL : postgres, pg
-- database : db, datastore
```

## **Performance Tuning**

### Оптимизация **GIN** индексов

```sql
-- Настройка параметров GIN
ALTER TABLE articles SET (
    gin_pending_list_limit = 1000
);

-- Перестроить индекс для оптимизации
REINDEX INDEX CONCURRENTLY idx_articles_body_gin;

-- Анализ использования индекса
ANALYZE articles;
```

### Оптимизация запросов

```sql
-- Использовать prepared statements
PREPARE search_articles(TEXT) AS
SELECT 
    id,
    title,
    ts_rank(body_tsvector, to_tsquery('english', $1)) AS rank
FROM articles
WHERE body_tsvector @@ to_tsquery('english', $1)
ORDER BY rank DESC
LIMIT 20;

-- Кэшировать результаты для популярных запросов
CREATE MATERIALIZED VIEW popular_searches AS
SELECT 
    query_text,
    array_agg(id ORDER BY rank DESC) AS article_ids
FROM (
    SELECT 
        'PostgreSQL' AS query_text,
        id,
        ts_rank(body_tsvector, to_tsquery('english', 'PostgreSQL')) AS rank
    FROM articles
    WHERE body_tsvector @@ to_tsquery('english', 'PostgreSQL')
    ORDER BY rank DESC
    LIMIT 20
) sub
GROUP BY query_text;

-- Обновлять материализованное представление
REFRESH MATERIALIZED VIEW CONCURRENTLY popular_searches;
```

## **Monitoring and Maintenance**

### Мониторинг производительности

```sql
-- Создать представление для мониторинга
CREATE VIEW fulltext_search_stats AS
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE indexname LIKE '%gin%'
ORDER BY idx_scan DESC;

-- Использовать представление
SELECT * FROM fulltext_search_stats;
```

### Обслуживание индексов

```sql
-- Функция для обслуживания GIN индексов
CREATE OR REPLACE FUNCTION maintain_gin_indexes()
RETURNS VOID AS $$
DECLARE
    idx_rec RECORD;
BEGIN
    FOR idx_rec IN
        SELECT indexname
        FROM pg_indexes
        WHERE indexdef LIKE '%USING gin%'
    LOOP
        EXECUTE format('REINDEX INDEX CONCURRENTLY %I', idx_rec.indexname);
        RAISE NOTICE 'Reindexed: %', idx_rec.indexname;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('maintain-gin-indexes', '0 3 * * 0', 
    'SELECT maintain_gin_indexes();');
```

## Решение проблем

### Проблема: Индекс не используется

```sql
-- Проверить статистику
ANALYZE articles;

-- Проверить план выполнения
EXPLAIN ANALYZE
SELECT * FROM articles
WHERE body_tsvector @@ to_tsquery('english', 'PostgreSQL');

-- Принудительно использовать индекс
SET enable_seqscan = off;
EXPLAIN ANALYZE
SELECT * FROM articles
WHERE body_tsvector @@ to_tsquery('english', 'PostgreSQL');
SET enable_seqscan = on;
```

### Проблема: Медленное обновление индекса

```sql
-- Включить fastupdate
ALTER INDEX idx_articles_body_gin SET (fastupdate = on);

-- Или отключить для лучшей производительности поиска
ALTER INDEX idx_articles_body_gin SET (fastupdate = off);
```

### Проблема: Большой размер индекса

```sql
-- Проверить размер индекса
SELECT 
    pg_size_pretty(pg_relation_size('idx_articles_body_gin')) AS index_size;

-- Оптимизировать индекс
REINDEX INDEX CONCURRENTLY idx_articles_body_gin;

-- Использовать частичный индекс
DROP INDEX idx_articles_body_gin;
CREATE INDEX idx_articles_body_gin ON articles 
USING gin(body_tsvector)
WHERE status = 'active';
```

## **Integration Examples**

### Интеграция с веб-приложением

```java
// PostgreSQL Python example replaced with Java Spring
// TODO: добавить пример
```

