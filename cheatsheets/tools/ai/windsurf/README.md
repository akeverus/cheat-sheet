---
title: "Windsurf"
description: "AI-first IDE от Codeium с агентом Cascade, inline-командами и глубокой интеграцией с терминалом и web search."
tags:
  - meta
  - index
  - tools
  - ai
  - windsurf
type: "index"
updated: "2026-04-17"
---
# Windsurf

Windsurf — AI-first IDE от Codeium. В отличие от «редактор + AI-плагин» Windsurf изначально спроектирован вокруг AI: **Command** для правок по описанию, **Cascade** — агентский чат-режим с доступом к терминалу и многофайловым правкам, встроенный **web search**, и Autocomplete, тонко настроенный под agent-first сценарии.

Для кого: разработчики, которые хотят попробовать AI-first IDE (альтернатива Cursor), команды с большим объёмом агентских задач (скаффолдинг, миграции, автоправки по issue), пользователи Codeium, переходящие с легковесного плагина на полноценную среду.

## Полезные ссылки

### Основные документы
- [[windsurf-basics]] — установка, Command, Cascade, web search, терминал, best practices

### Соседние разделы
- [[README|tools/ai/]] — родительский раздел
- [[README|tools/ai/cursor/]] — главный конкурент
- [[README|tools/ai/copilot/]] — плагин-альтернатива
- [[README|tools/ai/claude/]] — ассистент в VS Code/JetBrains

### Внешние ресурсы
- [Windsurf Documentation](https://docs.codeium.com/windsurf/getting-started)
- [Windsurf Overview](https://docs.codeium.com/command/windsurf-overview)
- [Web Search in Windsurf](https://docs.codeium.com/windsurf/web-search)
- [Terminal в Windsurf](https://docs.codeium.com/windsurf/terminal)

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- **Autocomplete** — быстрые inline-подсказки от Codeium.
- **Command** — inline-команды (правки по описанию в выделенном куске).
- **Cascade** — агентский режим: многошаговые задачи, терминал, web-поиск, правки в нескольких файлах.
- **Web search** — встроенный поиск, результаты попадают в контекст.
- **Глубокая интеграция с терминалом** — ошибки/вывод автоматически попадают в AI.
- Codeium-экосистема: плагины для других IDE также доступны.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [[README|Windsurf]] | Windsurf (свой IDE) | Да (Cascade) | Нет | Free + Pro + Teams |
| [[README|Cursor]] | Cursor (форк VS Code) | Да (Agent, Background Agent) | Да (BYOK) | Free + Pro |
| [[README|Claude Code]] | VS Code, JetBrains, CLI | Да | Нет | Подписка |
| [[README|GitHub Copilot]] | VS Code, JetBrains, Neovim, Xcode | Да | Нет | Подписка |
| [[README|Cody]] | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |
| [[README|Aider]] | Нет (CLI) | Частичный | Да | Open source + API |
| [[README|OpenClaw]] | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Хотите AI-first IDE, и Cursor не зашёл (другой UX, другая модель ценообразования, другая модель по умолчанию).
- Часто работаете с терминалом — интеграция вывода в AI удобна.
- Нужен встроенный web search без переключения на браузер.
- **Хуже подходит, если:** критична JetBrains-интеграция (Windsurf — свой IDE), нужны локальные модели (возьмите Aider/OpenClaw).

## Маршруты чтения

- **Первый день:** `windsurf-basics.md` Autocomplete + Command + Cascade на простой задаче.
- **Неделя:** Cascade для многошаговых задач + web search + команды в терминале.

## Куда идти дальше

- Прямой конкурент — [[README]]
- Если не готовы менять IDE — [[README]], [[README]]
- CLI-варианты — [[README]], [[README]]
- Обзор всех AI-инструментов — [[README]]
