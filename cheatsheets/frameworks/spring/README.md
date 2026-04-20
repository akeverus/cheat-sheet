---
title: "Spring (legacy)"
description: "Legacy-контейнер: исторические заметки по Spring Core/Boot/Data. Основной контент перенесён в frameworks/java-frameworks/spring/."
tags:
  - meta
  - index
  - spring
  - legacy
type: "index"
updated: "2026-04-20"
---
# Spring (legacy)

> **Внимание:** это legacy-папка. Основной, актуальный контент по Spring живёт в **[[README|frameworks/java-frameworks/spring/]]** — там разделённые документы по Spring Core, Boot, MVC, WebFlux, Data JPA, Security, Cloud, Kafka, Actuator и другим модулям.

Здесь остались исторические файлы трёх базовых тем (`spring-core.md`, `spring-boot.md`, `spring-data.md`) — они сохранены для обратной совместимости ссылок, но не развиваются. При любых правках добавляйте материал в каноничный раздел.

Для кого: читатели, пришедшие по старым ссылкам, и мейнтейнеры, которым нужно ориентироваться в legacy-структуре.

## Полезные ссылки

### Каноничный раздел (используйте его)
- [[README|Spring (актуальный раздел)]] — полный индекс
- [[spring-core|Spring Core]]
- [[spring-boot|Spring Boot]]
- [[spring-mvc|Spring MVC]]
- [[spring-webflux|Spring WebFlux]]
- [[spring-data-jpa|Spring Data JPA]]
- [[spring-security|Spring Security]]
- [[spring-cloud|Spring Cloud]]
- [[spring-actuator|Spring Actuator]]

### Legacy-файлы в этой папке
- [[spring-core]]
- [[spring-boot]]
- [[spring-data]]

### Соседние разделы
- [[README|Java Frameworks]]
- [[README|Frameworks]]

### Внешние ресурсы
- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/reference/)

## Содержание

- [Статус папки](#статус-папки)
- [Куда переехал контент](#куда-переехал-контент)
- [Маршруты чтения](#маршруты-чтения)
- [Что делать с legacy-ссылками](#что-делать-с-legacy-ссылками)

## Статус папки

- **Заморожена.** Новые документы сюда не добавляем.
- Битые внешние ссылки на `frameworks/spring/*.md` оставлены живыми как алиасы.
- При фактическом обновлении темы — пишем в `frameworks/java-frameworks/spring/` и при необходимости дополняем redirect-заметку здесь.

## Куда переехал контент

| Тема legacy | Актуальный документ |
|-------------|---------------------|
| `spring-core.md` | [[spring-core]] |
| `spring-boot.md` | [[spring-boot]] |
| `spring-data.md` | [[spring-data-jpa]] + [[spring-data-jdbc|spring-data-jdbc.md]] + [[spring-r2dbc|spring-r2dbc.md]] |
| Остальные модули | см. [[README]] |

## Маршруты чтения

- **Вы попали сюда по ссылке:** идите в [[README|каноничный раздел]] — там структура актуальна.
- **Архивный материал:** используйте файлы в этой папке только как исторический референс, не как руководство к действию.

## Что делать с legacy-ссылками

- При правке документа в другом месте репозитория — меняйте `frameworks/spring/...` на `frameworks/java-frameworks/spring/...`.
- Не добавляйте новые ссылки на эту папку: они считаются deprecated.
- Если встретили противоречие между legacy и каноничной версией — каноничная побеждает.
