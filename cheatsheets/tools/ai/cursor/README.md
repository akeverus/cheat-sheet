---
title: "Cursor"
description: "AI-first IDE (форк VS Code): Tab Completion, Inline Edit, Chat, Agent Mode и Background Agent."
tags:
  - meta
  - index
  - tools
  - ai
  - cursor
type: "index"
updated: "2026-04-20"
---
# Cursor

Cursor — **AI-first IDE**, форк VS Code с глубокой интеграцией моделей (Claude, GPT, Gemini, возможность подключить свои API-ключи). Сочетает классический редактор с мощными AI-режимами: Tab Completion, Inline Edit (`Cmd+K`), Chat, Agent Mode для многофайловых изменений и Background Agent — фоновый агент в облаке, работающий пока вы занимаетесь другим.

Для кого: разработчики, которым хочется, чтобы AI был первоклассным гражданином IDE, а не плагином; те, кому нужен сильный агентский режим; команды, готовые перейти с VS Code (плагины и темы совместимы).

## Полезные ссылки

### Основные документы
- [cursor-basics](cursor-basics.md) — установка, Tab/Inline Edit/Chat/Agent, правила проекта

### Соседние разделы
- [tools/ai/](../../../basics/README.md) — родительский раздел
- [tools/ai/windsurf/](../../../basics/README.md) — главный конкурент (тоже AI-first IDE)
- [tools/ai/copilot/](../../../basics/README.md) — плагин для обычных IDE
- [tools/ai/claude/](../../../basics/README.md) — ассистент без своего редактора

### Внешние ресурсы
- [Cursor Documentation](https://cursor.com/docs/)
- [Cursor Quick Start](https://docs.cursor.com/get-started/introduction)
- [Cursor CLI Reference](https://cursor.com/docs/cli/reference/parameters)
- [Cursor Rules](https://docs.cursor.com/context/rules-for-ai) — правила на уровне проекта

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- **Tab Completion** — контекстное автодополнение следующих правок (не только строки).
- **Inline Edit (`Cmd+K`)** — преобразование выделенного куска по промпту.
- **Chat** — панель с моделью, awareness файлов/проекта.
- **Agent Mode (`Cmd+I`)** — автономное выполнение многошаговых задач.
- **Background Agent** — задачи в облаке, без блокировки локальной IDE.
- **.cursorrules / rules** — инструкции и конвенции на уровне проекта.
- Поддержка BYOK (Bring Your Own Key): Anthropic/OpenAI/Google API-ключи.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [Cursor](../../../basics/README.md) | Cursor (форк VS Code) | Да (Agent Mode, Background Agent) | Да (через custom API keys / локальные эндпоинты) | Free + Pro ($20/мес) + Business |
| [Windsurf](../../../basics/README.md) | Windsurf | Да (Cascade) | Нет | Free + Pro |
| [Claude Code](../../../basics/README.md) | VS Code, JetBrains, CLI | Да | Нет | Подписка Anthropic |
| [GitHub Copilot](../../../basics/README.md) | VS Code, JetBrains, Neovim, Xcode | Да | Нет | Подписка GitHub |
| [Cody](../../../basics/README.md) | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |
| [Aider](../../../basics/README.md) | Нет (CLI) | Частичный | Да | Open source + API |
| [OpenClaw](../../../basics/README.md) | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Хотите AI как основную часть workflow, а не помощника — Cursor ближе всех.
- Нужны агентские сценарии: многофайловый рефакторинг, разбор задачи по плану.
- Готовы поменять редактор — миграция с VS Code безболезненна (импорт settings/extensions).
- **Хуже подходит, если:** JetBrains — основной IDE (Cursor — только VS-форк), нужна глубокая IntelliJ-интеграция (возьмите Copilot/Claude).

## Маршруты чтения

- **Первый день:** `cursor-basics.md` Tab + Inline Edit + Chat на реальной задаче.
- **Неделя:** + Agent Mode + .cursorrules в репо + Background Agent для длинных задач.
- **Команда:** shared rules, Workspace-настройки, политика выбора моделей.

## Куда идти дальше

- Прямой конкурент — [README](../../../basics/README.md)
- Если не готовы менять IDE — [README](../../../basics/README.md), [README](../../../basics/README.md)
- CLI-альтернатива — [README](../../../basics/README.md)
- Автономные агенты — [README](../../../basics/README.md)
