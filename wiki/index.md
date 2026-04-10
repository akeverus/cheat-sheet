---
type: meta
title: "Wiki Index"
updated: 2026-04-10
---

# Wiki Index

## Overview
- [[overview]] — архитектурный обзор проекта

## Modules
- [[Quiz Domain]] — чистая доменная модель (32 класса, mature)
- [[Quiz Persistence]] — JDBC-репозитории, миграции (12 классов, mature)
- [[Quiz App]] — Spring Boot приложение (developing)

## Components
- [[Database Schema]] — таблицы, FK, индексы, 14 миграций (mature)
- [[Security]] — Spring Security, token auth, rate limiting (mature)
- [[API Endpoints]] — REST API: training, insights, admin, export (mature)
- [[Configuration]] — AppProperties, профили, env variables (mature)
- [[Frontend UI]] — Thymeleaf + app.js (developing)
- [[Infrastructure Services]] — DiagramService, MarkdownRender, Search (developing)

## Flows
- [[Interview Flow]] — от старта сессии до завершения (mature)
- [[AI Pipeline]] — генерация вопросов и ответов через LLM (mature)
- [[Spaced Repetition Flow]] — SM-2 алгоритм повторения (developing)
- [[Question Import Flow]] — импорт из markdown (developing)

## Entities
- [[Spring Boot]] — фреймворк (seed)
- [[Caffeine]] — in-memory кэш (seed)
- [[Flyway]] — миграции БД (seed)
- [[Thymeleaf]] — шаблонизатор (seed)
- [[Spring AI]] — AI-интеграция (seed)

## Concepts
- [[SM-2 Algorithm]] — интервальное повторение с формулами (mature)
- [[Layered Architecture]] — ArchUnit правила (developing)
- [[Strategy Pattern]] — стратегии выбора вопросов (seed)
- [[Prompt Engineering]] — 14-step промпт для AI (mature)

## Decisions
- [[ADR-001 Multi-Module Gradle]] — разделение на 3 модуля
- [[ADR-002 SQLite Default]] — SQLite по умолчанию
- [[ADR-003 AI Provider Abstraction]] — абстракция OpenAI/DeepSeek

## Sources
_Добавляются при инжесте_

## Questions
_Добавляются при запросах_
