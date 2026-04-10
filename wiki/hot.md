---
type: meta
title: "Hot Cache"
updated: 2026-04-10
---

# Recent Context

## Last Updated
2026-04-10 — Deep ingest of full codebase (4 parallel agents)

## Key Recent Facts
- Spring Boot quiz app для подготовки к интервью: SM-2 spaced repetition + AI answer generation
- 3 модуля: quiz-domain (immutable records, 0 Spring deps), quiz-persistence (JDBC, SQLite/PostgreSQL), quiz-app (services, AI, UI)
- AI pipeline: AbstractAiClient → OpenAI (gpt-4.1-mini) / DeepSeek, prompt-first single-call, strict JSON
- SM-2: easeDelta = 0.1 - (5-grade)*(0.08 + (5-grade)*0.02), min EF = 1.3
- WrongAnswerFeedbackService: Observer pattern, @EventListener @Async, pre-generates on AnswerEvent
- Security: constant-time token comparison, sliding window rate limiter (Caffeine, 20 req/min)
- 14 Flyway migrations, DB schema: questions → answer_options, review_state, hints (all CASCADE)
- UI: Thymeleaf + vanilla JS (app.js), 4 parallel async AI insight requests on result page

## Recent Changes
- Created 6 new component pages: Database Schema, Security, API Endpoints, Configuration, Frontend UI, Infrastructure Services
- Upgraded 6 pages to mature: Quiz Domain, Quiz Persistence, SM-2 Algorithm, AI Pipeline, Interview Flow, Prompt Engineering
- Total wiki pages: 34

## Active Threads
- Entity pages (Spring Boot, Caffeine, Flyway, Thymeleaf, Spring AI) still at seed status
- Quiz App module page needs deep update
- Obsidian MCP connected (MCPVault filesystem mode)
