---
type: meta
title: "Activity Log"
updated: 2026-04-10
---

# Activity Log

## [2026-04-10] ingest | Full codebase deep analysis
- Source: all Java files in quiz-domain, quiz-persistence, quiz-app modules
- 4 parallel research agents: interview services, AI/config, domain/persistence, UI/tests
- Pages created: [[Database Schema]], [[Security]], [[API Endpoints]], [[Configuration]], [[Frontend UI]], [[Infrastructure Services]]
- Pages updated (seed → mature): [[Quiz Domain]], [[Quiz Persistence]], [[SM-2 Algorithm]], [[AI Pipeline]], [[Interview Flow]], [[Prompt Engineering]]
- Sub-indexes created: components/_index, flows/_index
- Key insights:
  - SM-2 с формулами: easeDelta = 0.1 - (5-grade)*(0.08 + (5-grade)*0.02)
  - WrongAnswerFeedbackService uses Observer pattern: @EventListener @Async pre-generates на AnswerEvent
  - Security: constant-time token comparison, sliding window rate limiter (Caffeine-based)
  - 14-step prompt methodology в general.txt

## [2026-04-10] scaffold | Initial wiki structure
- Mode: B (Repository/Codebase)
- Created full wiki scaffold from codebase analysis
- Pages created: overview, 3 modules, 4 flows, 5 entities, 4 concepts, 3 ADRs
- Templates: 6 Templater templates
- Visual: vault-colors.css
