---
type: meta
title: "Wiki Index"
updated: 2026-04-10
---

# Wiki Index

## Overview
- [[overview]] — архитектурный обзор всего проекта

## Modules
- [[Quiz Domain]] — чистая доменная модель (32 класса)
- [[Quiz Persistence]] — JDBC-репозитории, миграции (12 классов)
- [[Quiz App]] — Spring Boot приложение (контроллеры, сервисы, AI)

## Flows
- [[Interview Flow]] — основной поток интервью
- [[AI Pipeline]] — генерация вопросов и ответов через LLM
- [[Spaced Repetition Flow]] — SM-2 алгоритм
- [[Question Import Flow]] — импорт из markdown

## Entities
- [[Spring Boot]] — фреймворк приложения
- [[Caffeine]] — in-memory кэш
- [[Flyway]] — миграции БД
- [[Thymeleaf]] — шаблонизатор
- [[Spring AI]] — AI-интеграция

## Concepts
- [[SM-2 Algorithm]] — алгоритм интервального повторения
- [[Layered Architecture]] — слоёная архитектура с ArchUnit
- [[Strategy Pattern]] — выбор вопросов
- [[Prompt Engineering]] — промпты для AI-генерации

## Decisions
- [[ADR-001 Multi-Module Gradle]] — разделение на 3 модуля
- [[ADR-002 SQLite Default]] — SQLite по умолчанию, PostgreSQL опционально
- [[ADR-003 AI Provider Abstraction]] — абстракция над OpenAI/DeepSeek

## Sources
_Добавляются при инжесте_

## Questions
_Добавляются при запросах_
