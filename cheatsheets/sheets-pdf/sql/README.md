---
title: "SQL Cheat Sheets (PDF)"
description: "Набор офлайн-шпаргалок по SQL в PDF: базовые команды, соединения, оконные функции, анализ данных."
tags:
  - meta
  - index
  - sheets-pdf
  - sql
type: "index"
updated: "2026-04-20"
---
# SQL Cheat Sheets (PDF)

Подпапка `sheets-pdf/sql/` содержит компактные PDF-шпаргалки по SQL для офлайн-использования и быстрой подготовки к собеседованиям. Это дополнение к markdown-материалам в `databases/sql/` — PDF удобно распечатать или держать на втором мониторе во время работы.

Каждый PDF покрывает одну узкую тему: синтаксис DDL/DML, `JOIN`, оконные функции или задачи аналитики. Тематически они привязаны к основному разделу по SQL и индексируются в `sheets-pdf/INDEX.yaml`.

## Полезные ссылки

### Файлы в этой папке
- [sql-basic-commands.pdf](sql-basic-commands.pdf) — базовые команды: `SELECT`, `INSERT`, `UPDATE`, `DELETE`, DDL, агрегаты, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`, `DISTINCT`
- [sql-joins.pdf](sql-joins.pdf) — `INNER`, `LEFT`, `RIGHT`, `FULL OUTER`, `CROSS`, `SELF`-соединения, семантика и примеры
- [sql-window-functions.pdf](sql-window-functions.pdf) — оконные функции: `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`, `LEAD`, `NTILE`, `OVER (PARTITION BY ... ORDER BY ...)`, frame clause
- [sql-data-analysis.pdf](sql-data-analysis.pdf) — аналитические запросы: когортный анализ, retention, pivot/unpivot, CTE, `GROUP BY ROLLUP/CUBE`

### Связанные markdown-документы
- [[README|databases/sql/]] — основной раздел по SQL
- [[sql-basics|databases/sql/sql-basics.md]] — базовый синтаксис (соответствует `sql-basic-commands.pdf`)
- [[README|databases/relational/]] — реляционные СУБД
- [[sql-interview|interview/databases/sql-interview.md]] — вопросы по SQL на собеседовании

### Соседние разделы
- [[README|sheets-pdf (корень)]]
- [INDEX.yaml](../INDEX.yaml) — машиночитаемый индекс всех PDF

### Внешние ресурсы
- [PostgreSQL SQL Reference](https://www.postgresql.org/docs/current/sql.html)
- [SQL-92, SQL:2003 стандарты](https://en.wikipedia.org/wiki/SQL#Standardization_history)
- [Use The Index, Luke](https://use-the-index-luke.com/) — индексы и производительность

## Содержание

- [Карта PDF и покрываемых тем](#карта-pdf-и-покрываемых-тем)
- [Когда какой PDF использовать](#когда-какой-pdf-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта PDF и покрываемых тем

| PDF | Темы SQL | Уровень |
|-----|----------|---------|
| [sql-basic-commands.pdf](sql-basic-commands.pdf) | DDL (`CREATE`, `ALTER`, `DROP`), DML (`SELECT`, `INSERT`, `UPDATE`, `DELETE`), фильтрация, агрегаты, группировка, сортировка, `DISTINCT`, базовые типы | Junior |
| [sql-joins.pdf](sql-joins.pdf) | `INNER JOIN`, `LEFT/RIGHT/FULL OUTER JOIN`, `CROSS JOIN`, `SELF JOIN`, `USING`, `NATURAL JOIN`, семантика на диаграммах | Junior / Middle |
| [sql-window-functions.pdf](sql-window-functions.pdf) | `OVER`, `PARTITION BY`, `ORDER BY`, frame (`ROWS`/`RANGE`), `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`/`LEAD`, `NTILE`, агрегаты с окном | Middle / Senior |
| [sql-data-analysis.pdf](sql-data-analysis.pdf) | CTE и рекурсивные CTE, `GROUP BY ROLLUP/CUBE/GROUPING SETS`, подзапросы, когортный анализ, retention, pivot, `CASE`-агрегаты | Middle / Senior |

## Когда какой PDF использовать

- **Учу SQL с нуля** — `sql-basic-commands.pdf` + [[sql-basics|databases/sql/sql-basics.md]].
- **Запутался в JOIN-ах** — `sql-joins.pdf` как референс под рукой.
- **Нужно посчитать rank / top-N per group** — `sql-window-functions.pdf`.
- **Аналитический запрос для дашборда/отчёта** — `sql-data-analysis.pdf`.
- **Собеседование с SQL-задачами** — все четыре PDF + [[sql-interview|interview/databases/sql-interview.md]].

## Маршруты чтения

- **Быстрый повтор перед собеседованием (1 вечер):** `sql-basic-commands` -> `sql-joins` -> `sql-window-functions`.
- **Middle-to-senior апгрейд:** `sql-window-functions` -> `sql-data-analysis` + практика на реальных данных в PostgreSQL.
- **Разработка отчётов:** `sql-data-analysis` + оконные функции + CTE; затем производительность в [[README|databases/sql/]].

## Куда идти дальше

- Основной раздел SQL — [[README]]
- PostgreSQL и диалекты — [[README|../../databases/relational/postgresql/]]
- SQL на собеседовании — [[sql-interview]]
- Корень PDF-каталога — [[README]] и [../INDEX.yaml](../INDEX.yaml)
