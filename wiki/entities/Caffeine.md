---
type: entity
title: "Caffeine"
entity_type: library
created: 2026-04-10
updated: 2026-04-10
tags:
  - entity
  - cache
  - library
status: seed
---

# Caffeine

In-memory кэш для вариантов ответов.

## Конфигурация

- Max: 500 элементов
- TTL: 1 час
- Реализация: `CaffeineOptionCache` implements `OptionCache`

## Связи

- Кэширует результаты [[AI Pipeline]]
- Конфигурируется в `CacheConfig`
