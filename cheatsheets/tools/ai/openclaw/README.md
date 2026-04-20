---
title: "OpenClaw"
description: "Open-source framework для автономных AI-агентов и coding assistant: локальные модели, инструменты, CLI-хуки."
tags:
  - meta
  - index
  - tools
  - ai
  - openclaw
type: "index"
updated: "2026-04-20"
---
# OpenClaw

OpenClaw — open-source фреймворк для автономных AI-агентов и coding assistant, ориентированный на разработчиков. Позволяет описывать задачи естественным языком, разбивает их на подзадачи и выполняет автономно, используя встроенные tools: файловая система, web search, выполнение кода, браузер, HTTP, БД. Ключевые плюсы — **полная локальная работа через Ollama/GGUF**, мульти-провайдерность и отсутствие платной подписки.

Для кого: инженеры, которым нужна полная приватность (сенсорные данные, закрытые сети); команды, строящие кастомные AI-инструменты поверх CLI/хуков; энтузиасты автономных агентов, готовые к возне с конфигурацией.

## Полезные ссылки

### Основные документы
- [openclaw-basics](openclaw-basics.md) — установка, CLI, задачи, агенты, хуки, интеграции

### Соседние разделы
- [tools/ai/](../../../basics/README.md) — родительский раздел
- [tools/ai/aider/](../../../basics/README.md) — более простая CLI-альтернатива
- [tools/ai/cursor/](../../../basics/README.md) — агенты в IDE

### Внешние ресурсы
- [OpenClaw GitHub](https://github.com/open-claw/openclaw)
- [Ollama](https://ollama.com/) — локальные модели
- [LangChain](https://www.langchain.com/) — близкий по духу фреймворк

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- Задачи на естественном языке, автоматическая декомпозиция.
- Поддержка OpenAI, Anthropic, Mistral, LLaMA 3, Gemini — и любых OpenAI-совместимых API.
- Полностью локальный режим через Ollama или GGUF (llama.cpp).
- Встроенные tools: файлы, web search, code execution, browser, HTTP, SQL.
- Хуки и CLI-интеграция — можно встраивать в пайплайны и shell-скрипты.
- Open source — можно доработать под свои нужды.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [OpenClaw](../../../basics/README.md) | Нет (CLI/framework) | Да (автономные агенты, multi-step) | Да (Ollama, GGUF) | Open source |
| [Aider](../../../basics/README.md) | Нет (CLI) | Частичный (architect/editor) | Да (Ollama) | Open source + API |
| [Claude Code](../../../basics/README.md) | VS Code, JetBrains, CLI | Да | Нет | Подписка |
| [Cursor](../../../basics/README.md) | Cursor | Да (Agent, Background Agent) | Да (BYOK) | Free + Pro |
| [GitHub Copilot](../../../basics/README.md) | VS Code, JetBrains, Neovim | Да (Copilot Agent) | Нет | Подписка |
| [Windsurf](../../../basics/README.md) | Windsurf | Да (Cascade) | Нет | Free + Pro |
| [Cody](../../../basics/README.md) | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |

## Когда использовать

- Полностью offline / закрытая сеть / регуляторные ограничения.
- Нужно построить собственный агент под узкую задачу (batch-миграция, triage багов, парс репозиториев).
- Интеграция в CI/CD — автономные агенты в пайплайне.
- **Хуже подходит, если:** нужен «готовый продукт» с IDE-UX (возьмите Cursor/Copilot), ваша команда не готова заниматься конфигом и поддержкой фреймворка.

## Маршруты чтения

- **Быстрый старт:** `openclaw-basics.md` установить Ollama запустить простую задачу локально.
- **Продвинутый:** встроить OpenClaw-хуки в CI автономный агент для рутинных правок.

## Куда идти дальше

- Более простой CLI-вариант — [README](../../../basics/README.md)
- Агенты в IDE — [README](../../../basics/README.md)
- IDE-ассистент с длинным контекстом — [README](../../../basics/README.md)
- Обзор и выбор AI-инструмента — [README](../../../basics/README.md)
