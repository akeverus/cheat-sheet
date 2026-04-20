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
updated: "2026-04-17"
---
# Cursor

Cursor — **AI-first IDE**, форк VS Code с глубокой интеграцией моделей (Claude, GPT, Gemini, возможность подключить свои API-ключи). Сочетает классический редактор с мощными AI-режимами: Tab Completion, Inline Edit (`Cmd+K`), Chat, Agent Mode для многофайловых изменений и Background Agent — фоновый агент в облаке, работающий пока вы занимаетесь другим.

Для кого: разработчики, которым хочется, чтобы AI был первоклассным гражданином IDE, а не плагином; те, кому нужен сильный агентский режим; команды, готовые перейти с VS Code (плагины и темы совместимы).

## Полезные ссылки

### Основные документы
- [[cursor-basics]] — установка, Tab/Inline Edit/Chat/Agent, правила проекта

### Соседние разделы
- [[README|tools/ai/]] — родительский раздел
- [[README|tools/ai/windsurf/]] — главный конкурент (тоже AI-first IDE)
- [[README|tools/ai/copilot/]] — плагин для обычных IDE
- [[README|tools/ai/claude/]] — ассистент без своего редактора

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
| [[README|Cursor]] | Cursor (форк VS Code) | Да (Agent Mode, Background Agent) | Да (через custom API keys / локальные эндпоинты) | Free + Pro ($20/мес) + Business |
| [[README|Windsurf]] | Windsurf | Да (Cascade) | Нет | Free + Pro |
| [[README|Claude Code]] | VS Code, JetBrains, CLI | Да | Нет | Подписка Anthropic |
| [[README|GitHub Copilot]] | VS Code, JetBrains, Neovim, Xcode | Да | Нет | Подписка GitHub |
| [[README|Cody]] | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |
| [[README|Aider]] | Нет (CLI) | Частичный | Да | Open source + API |
| [[README|OpenClaw]] | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Хотите AI как основную часть workflow, а не помощника — Cursor ближе всех.
- Нужны агентские сценарии: многофайловый рефакторинг, разбор задачи по плану.
- Готовы поменять редактор — миграция с VS Code безболезненна (импорт settings/extensions).
- **Хуже подходит, если:** JetBrains — основной IDE (Cursor — только VS-форк), нужна глубокая IntelliJ-интеграция (возьмите Copilot/Claude).

## Маршруты чтения

- **Первый день:** `cursor-basics.md` → Tab + Inline Edit + Chat на реальной задаче.
- **Неделя:** + Agent Mode + .cursorrules в репо + Background Agent для длинных задач.
- **Команда:** shared rules, Workspace-настройки, политика выбора моделей.

## Куда идти дальше

- Прямой конкурент — [[README]]
- Если не готовы менять IDE — [[README]], [[README]]
- CLI-альтернатива — [[README]]
- Автономные агенты — [[README]]
