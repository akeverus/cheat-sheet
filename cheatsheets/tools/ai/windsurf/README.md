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
updated: "2026-04-20"
---
# Windsurf

Windsurf — AI-first IDE от Codeium. В отличие от «редактор + AI-плагин» Windsurf изначально спроектирован вокруг AI: **Command** для правок по описанию, **Cascade** — агентский чат-режим с доступом к терминалу и многофайловым правкам, встроенный **web search**, и Autocomplete, тонко настроенный под agent-first сценарии.

Для кого: разработчики, которые хотят попробовать AI-first IDE (альтернатива Cursor), команды с большим объёмом агентских задач (скаффолдинг, миграции, автоправки по issue), пользователи Codeium, переходящие с легковесного плагина на полноценную среду.

## Полезные ссылки

### Основные документы
- [windsurf-basics](windsurf-basics.md) — установка, Command, Cascade, web search, терминал, best practices

### Соседние разделы
- [tools/ai/](../../../basics/README.md) — родительский раздел
- [tools/ai/cursor/](../../../basics/README.md) — главный конкурент
- [tools/ai/copilot/](../../../basics/README.md) — плагин-альтернатива
- [tools/ai/claude/](../../../basics/README.md) — ассистент в VS Code/JetBrains

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
| [Windsurf](../../../basics/README.md) | Windsurf (свой IDE) | Да (Cascade) | Нет | Free + Pro + Teams |
| [Cursor](../../../basics/README.md) | Cursor (форк VS Code) | Да (Agent, Background Agent) | Да (BYOK) | Free + Pro |
| [Claude Code](../../../basics/README.md) | VS Code, JetBrains, CLI | Да | Нет | Подписка |
| [GitHub Copilot](../../../basics/README.md) | VS Code, JetBrains, Neovim, Xcode | Да | Нет | Подписка |
| [Cody](../../../basics/README.md) | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |
| [Aider](../../../basics/README.md) | Нет (CLI) | Частичный | Да | Open source + API |
| [OpenClaw](../../../basics/README.md) | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Хотите AI-first IDE, и Cursor не зашёл (другой UX, другая модель ценообразования, другая модель по умолчанию).
- Часто работаете с терминалом — интеграция вывода в AI удобна.
- Нужен встроенный web search без переключения на браузер.
- **Хуже подходит, если:** критична JetBrains-интеграция (Windsurf — свой IDE), нужны локальные модели (возьмите Aider/OpenClaw).

## Маршруты чтения

- **Первый день:** `windsurf-basics.md` Autocomplete + Command + Cascade на простой задаче.
- **Неделя:** Cascade для многошаговых задач + web search + команды в терминале.

## Куда идти дальше

- Прямой конкурент — [README](../../../basics/README.md)
- Если не готовы менять IDE — [README](../../../basics/README.md), [README](../../../basics/README.md)
- CLI-варианты — [README](../../../basics/README.md), [README](../../../basics/README.md)
- Обзор всех AI-инструментов — [README](../../../basics/README.md)
