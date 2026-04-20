---
title: "Computer Science"
description: "Точка входа в раздел Computer Science: карта тем фундамента (сложность, структуры данных, архитектура, OC, сети, криптография) с переходами в смежные домены."
tags:
  - meta
  - index
  - computer-science
type: "index"
updated: "2026-04-20"
---
# Computer Science

Раздел покрывает ядро компьютерных наук: теорию вычислений и сложности, архитектуру компьютеров, иерархию памяти, базовые структуры данных и алгоритмы, основы операционных систем, сетей, БД и криптографии. Используется как контекст для более глубоких доменов (`algorithms/`, `databases/`, `basics/networks/`, `basics/operating-systems/`).

Для кого: инженеры, которые готовятся к собеседованиям по fundamentals или восстанавливают карту связей между CS-областями. Одна большая шпаргалка собирает обзор всех тем сразу.

## Полезные ссылки

### Основной документ
- [Основы Computer Science](computer-science-basics.md) — полный обзор раздела

### Соседние разделы
- [Основы программирования](../README.md)
- [Компьютерные сети](../README.md)
- [Операционные системы](../README.md)
- [Алгоритмы и структуры данных](../README.md)
- [Базы данных](../README.md)
- [Безопасность](../README.md)

### Внешние ресурсы
- [Computer Science Field Guide](https://www.csfieldguide.org.nz/) — интерактивное руководство по CS
- [MIT OCW: 6.006 Introduction to Algorithms](https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-spring-2020/)
- [Baeldung CS](https://www.baeldung.com/cs) — подборка CS-тем с примерами

## Содержание

- [Карта тем](#карта-тем)
- [Что внутри документа](#что-внутри-документа)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Где глубже |
|------|-----------|
| Вычислительная сложность, P vs NP, теория информации | [computer-science-basics](computer-science-basics.md#вычислительная-сложность) |
| Структуры данных (массивы, списки, деревья, хэш-таблицы, графы) | [algorithms/data-structures/](../README.md) |
| Алгоритмы сортировки и поиска | [algorithms/sorting/](../README.md), [algorithms/searching/](../README.md) |
| Архитектура компьютеров, иерархия памяти, параллельные вычисления | [computer-science-basics](computer-science-basics.md#архитектура-компьютеров) |
| Процессы, потоки, планирование | [basics/operating-systems/](../README.md) |
| TCP/IP, OSI, прикладные протоколы | [basics/networks/](../README.md) |
| Реляционная модель, нормализация | [databases/relational/](../README.md) |
| Криптография | [security/data/](../README.md) |

## Что внутри документа

[computer-science-basics](computer-science-basics.md) содержит:

- Обзор основных областей CS и их связей
- Классы сложности P, NP, NP-complete, NP-hard и примеры задач
- Теория информации: энтропия Шеннона, избыточность
- Архитектура фон Неймана, CPU/память/шины, иерархия кэшей
- Параллельные вычисления: law of Amdahl, thread vs process
- Введение в процессы, потоки, IPC
- Реляционная модель, нормальные формы (1NF-BCNF)
- Модель OSI и TCP/IP, базовые протоколы
- Симметричная/асимметричная криптография, хэши
- Машины Тьюринга, теорема Гёделя, вычислимость

## Маршруты чтения

- **Quick scan (30 мин):** `Введение Архитектура компьютеров Иерархия памяти OSI/TCP-IP Частые вопросы`.
- **Подготовка к интервью fundamentals (2-3 ч):** весь документ + `algorithms/README.md`.
- **Углубление OS/networks:** сначала разделы из `basics/operating-systems/` и `basics/networks/`, затем смежные темы в `computer-science-basics.md` для контекста.

## Куда идти дальше

- Алгоритмические паттерны и сложность — [algorithms/algorithmic-paradigms/](../README.md)
- Практика JVM и памяти — [languages/java/](../README.md)
- Производительность запросов и индексы — [databases/sql/](../README.md)
- Конкурентность и синхронизация — [patterns/concurrency-patterns/](../README.md)
