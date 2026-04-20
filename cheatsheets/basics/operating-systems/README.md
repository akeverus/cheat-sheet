---
title: "Операционные системы"
description: "Точка входа в раздел ОС: ядро, процессы и потоки, планирование, память, файловые системы, I/O, синхронизация и безопасность."
tags:
  - meta
  - index
  - operating-systems
type: "index"
updated: "2026-04-17"
---
# Операционные системы

Раздел описывает устройство ОС с упором на Linux: архитектуру ядра (monolithic, microkernel, hybrid), жизненный цикл и планирование процессов, управление памятью и виртуальную память, файловые системы, I/O-модели, примитивы синхронизации и безопасность.

Для кого: бэкенд-инженеры, которые хотят понимать, почему JVM-процесс жрёт swap, как работает epoll под NIO/Netty, зачем нужен cgroups в контейнерах и где возникает contention.

## Полезные ссылки

### Основной документ
- [[operating-systems-basics|Операционные системы: основы]]
- [[linux-handbook|Linux и Bash: практический справочник]] — рабочий справочник: команды, права, systemd, production-troubleshooting

### Соседние разделы
- [[README|Computer Science]]
- [[README|Компьютерные сети]]
- [[README|Паттерны конкурентности]]
- [[README|Docker]]
- [[README|Kubernetes]]
- [[README|Java: concurrency]]

### Внешние ресурсы
- [Linux Kernel Documentation](https://www.kernel.org/doc/html/latest/)
- [Operating System Concepts (Silberschatz)](https://www.os-book.com/OS10/)
- [Linux Insides](https://0xax.gitbooks.io/linux-insides/content/) — разбор устройства ядра
- [Brendan Gregg — Linux Performance](http://www.brendangregg.com/linuxperf.html)

## Содержание

- [Карта тем](#карта-тем)
- [Что внутри документа](#что-внутри-документа)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Подсистема | Что изучать | Инструменты диагностики |
|-----------|-------------|-------------------------|
| Процессы/потоки | fork/exec, PCB, состояния, zombie/orphan | `ps`, `top`, `htop`, `pstree` |
| Планирование | CFS, приоритеты, nice, affinity | `schedtool`, `taskset`, `/proc/<pid>/sched` |
| Память | Virtual memory, page table, swap, OOM | `free`, `vmstat`, `smem`, `/proc/meminfo` |
| Файловые системы | ext4, XFS, inode, journaling, mount | `df`, `du`, `lsof`, `iostat` |
| I/O | blocking/non-blocking, select/poll/epoll, aio | `strace`, `iotop`, `blktrace` |
| Синхронизация | mutex, semaphore, spinlock, futex | `perf`, `ftrace` |
| Безопасность | PAM, SELinux/AppArmor, capabilities | `getcap`, `auditctl` |

## Что внутри документа

[[operating-systems-basics]] содержит:

- Архитектуры ядра: monolithic (Linux), microkernel (MINIX, L4), hybrid (Darwin, NT)
- Жизненный цикл процесса: создание, планирование, выход, очистка
- Алгоритмы планирования: FCFS, SJF, Round-Robin, Priority, CFS
- Виртуальная память: paging, TLB, page fault, swap
- Файловые системы: inode-таблица, журналирование, права доступа
- Ввод-вывод: драйверы, character/block devices, DMA
- Синхронизация: race condition, deadlock, starvation, примитивы
- Аутентификация/авторизация, ACL, capabilities

## Маршруты чтения

- **Fundamentals для backend (1 ч):** `Процессы/потоки Планирование Виртуальная память I/O`.
- **Диагностика prod-инцидента:** `Решение проблем Память/OOM I/O` + утилиты из таблицы.
- **Подготовка к собеседованию:** вся карта + [[README|patterns/concurrency-patterns/]].

## Куда идти дальше

- Java Memory Model и GC — [[README|languages/java/]]
- Контейнеры: namespaces, cgroups — [[README|platform/containers/docker/]]
- Observability процессов — [[README|monitoring/metrics/]]
- Системные вызовы и сетевой стек — [[README|basics/networks/]]
