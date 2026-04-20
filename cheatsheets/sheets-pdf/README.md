# Sheets PDF: каталог артефактов

Каталог `cheatsheets/sheets-pdf/` хранит внешние PDF-материалы как отдельный артефактный слой проекта.

## Зачем нужен этот каталог

- Быстрый офлайн-доступ к компактным шпаргалкам.
- Дополнение к структурированным markdown-документам в `cheatsheets/`.
- Явная связка PDF -> тематические разделы через `INDEX.yaml`.

## Как пользоваться

- Для каждого PDF смотри соответствие в `INDEX.yaml`.
- Если добавляешь новый PDF:
  1) положи файл в `cheatsheets/sheets-pdf/` или подходящую подпапку;
  2) добавь запись в `INDEX.yaml`;
  3) привяжи файл к одному или нескольким путям из `cheatsheets/`.

## Правила

- Запрещено добавлять PDF без индексации в `INDEX.yaml`.
- Переименование файла требует обновления поля `file` в `INDEX.yaml`.
- `related_cheatsheets` должен указывать на существующие разделы.

## См. также

- [Архитектура и правила cheatsheets](../CHEATSHEETS_ARCHITECTURE_AND_RULES.md)
- [Java Basics](../languages/java/java-basics.md)
- [PostgreSQL Basics](../databases/relational/postgresql/postgres-basics.md)
- [Spring Boot](../frameworks/spring/spring-boot.md)
- [Алгоритмы](../algorithms/problems/algorithms.md)
