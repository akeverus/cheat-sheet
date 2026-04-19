---
title: "Claude Code"
description: "AI-ассистент Anthropic для VS Code, JetBrains и терминала: длинный контекст, агентский режим, аккуратные диффы."
tags:
  - meta
  - index
  - tools
  - ai
  - claude
type: "index"
updated: "2026-04-17"
---
# Claude Code

Claude Code — AI-ассистент Anthropic, встраиваемый в VS Code, JetBrains-IDE и CLI. Сильные стороны — **длинный контекст** (сотни тысяч токенов, у Opus 4.7 — до 1M), качественные диффы, аналитический режим (объяснение сложного кода, рефакторинг, разбор архитектуры) и полноценный агентский режим с доступом к терминалу и файлам.

Для кого: разработчики, которым нужен IDE-ассистент для больших репозиториев, инженеры на ревью и рефакторинге legacy-кода, а также те, кому важны агентские сценарии (автономный запуск команд, батч-правки) с контролируемыми пермишенами.

## Полезные ссылки

### Основные документы
- [claude-basics.md](claude-basics.md) — установка, режимы работы, длинный контекст, CLI, best practices

### Соседние разделы
- [tools/ai/](../README.md) — родительский раздел
- [tools/ai/cursor/](../cursor/README.md) — AI-first IDE-альтернатива
- [tools/ai/copilot/](../copilot/README.md) — главный конкурент от GitHub
- [tools/ai/aider/](../aider/README.md) — CLI-вариант

### Внешние ресурсы
- [Claude Code Documentation](https://docs.claude.com/en/docs/claude-code/ide-integrations)
- [Claude Code для VS Code](https://marketplace.visualstudio.com/items?itemName=Anthropic.claude-code)
- [Claude Code для JetBrains](https://docs.claude.com/en/docs/claude-code/jetbrains)
- [Claude Code CLI Reference](https://code.claude.com/docs/en/cli-reference)

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- Chat с awareness IDE-контекста: открытые файлы, выделения, диапазоны строк через `@file:lines`.
- Длинный контекст — можно скормить десятки файлов без обрезки.
- Агентский режим с Bash/Edit/Read/Grep tools — правит код, запускает тесты, коммитит.
- Plan Mode — сначала план, потом исполнение; удобно для сложных задач.
- Hooks и skills — кастомизация поведения на уровне проекта.
- CLI (`claude` команда) — для автоматизации и CI.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [Claude Code](../claude/README.md) | VS Code, JetBrains, CLI | Да (agent, terminal-tools) | Нет (Anthropic API) | Подписка Anthropic |
| [GitHub Copilot](../copilot/README.md) | VS Code, JetBrains, Neovim, Xcode | Да (Copilot Agent, Spaces) | Нет | Подписка GitHub |
| [Cursor](../cursor/README.md) | Cursor (форк VS Code) | Да (Agent, Background Agent) | Да (custom API) | Free + Pro |
| [Windsurf](../windsurf/README.md) | Windsurf | Да (Cascade) | Нет | Free + Pro |
| [Cody](../cody/README.md) | VS Code, JetBrains | Частичный | Enterprise: да | Free + Pro/Enterprise |
| [Aider](../aider/README.md) | Нет (CLI) | Частичный | Да (Ollama) | Open source + API |
| [OpenClaw](../openclaw/README.md) | Нет (CLI) | Да (автономные) | Да | Open source |

## Когда использовать

- Большие репозитории, много контекста — длинный контекст Claude даёт качественный рефакторинг.
- Аналитические задачи: «объясни этот кусок legacy», «предложи архитектурный рефакторинг».
- Агентские сценарии: «запусти тесты, почини флейки, сделай PR».
- **Хуже подходит, если:** нужна offline-работа (нет локальных моделей), или бюджет жёстко фиксирован и команда уже в экосистеме GitHub (Copilot может выйти дешевле).

## Маршруты чтения

- **Первый день:** `claude-basics.md` → раздел Установка → Chat + диффы → Plan Mode на простой задаче.
- **Продвинутый:** `claude-basics.md` полностью + CLI + hooks + skills в `~/.claude/`.
- **Команда:** `claude-basics.md` + организация custom instructions и shared skills.

## Куда идти дальше

- Сравнение и выбор инструмента — [../README.md](../README.md)
- Альтернатива от GitHub — [../copilot/README.md](../copilot/README.md)
- AI-first IDE — [../cursor/README.md](../cursor/README.md)
- CLI-вариант — [../aider/README.md](../aider/README.md)
