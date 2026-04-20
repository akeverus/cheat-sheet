---
title: "Sourcegraph Cody"
description: "AI-ассистент с глубокой интеграцией в Sourcegraph code graph: контекст из большого репозитория, команды и Prompt Library."
tags:
  - meta
  - index
  - tools
  - ai
  - cody
type: "index"
updated: "2026-04-20"
---
# Sourcegraph Cody

Cody — AI-ассистент от Sourcegraph, чья ключевая особенность — **контекст из code graph**: ассистент опирается не только на открытые файлы, но и на индекс всего репозитория (и даже нескольких). Это особенно полезно в монорепо и больших кодобазах, где «что где лежит» — нетривиальный вопрос. Поддерживает команды, Prompt Library для командного переиспользования и интеграцию с VS Code/JetBrains.

Для кого: команды с большим монорепо или множеством репозиториев, где важен cross-repo контекст; организации, уже использующие Sourcegraph для поиска по коду; инженеры, которые хотят стандартизовать промпты в команде через Prompt Library.

## Полезные ссылки

### Основные документы
- [[cody-basics]] — установка, чат, команды, управление контекстом, Prompt Library

### Соседние разделы
- [[README|tools/ai/]] — родительский раздел
- [[README|tools/ai/copilot/]] — альтернатива с меньшим акцентом на код-граф
- [[README|tools/ai/claude/]] — длинный контекст через окно модели

### Внешние ресурсы
- [Cody Documentation](https://sourcegraph.com/docs/cody)
- [Cody Chat](https://sourcegraph.com/docs/cody/capabilities/chat)
- [Cody Commands](https://sourcegraph.com/docs/cody/capabilities/commands)
- [Cody Prompting Guide](https://sourcegraph.com/docs/cody/prompts-guide)

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- Чат с контекстом из проиндексированного кода (репозитория/организации).
- Команды (Commands) — переиспользуемые шаблоны: «Explain», «Smell», «Test», кастомные.
- Prompt Library — командный репозиторий промптов.
- Управление контекстом через `@` (файл, символ, репозиторий).
- Enterprise-дистрибутив с поддержкой self-hosted моделей и приватных LLM.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [[README|Cody]] | VS Code, JetBrains | Частичный (commands) | Да (Enterprise/self-hosted) | Free + Pro + Enterprise |
| [[README|Claude Code]] | VS Code, JetBrains, CLI | Да | Нет | Подписка Anthropic |
| [[README|GitHub Copilot]] | VS Code, JetBrains, Neovim, Xcode | Да | Нет | Подписка GitHub |
| [[README|Cursor]] | Cursor | Да | Да (custom API) | Free + Pro |
| [[README|Aider]] | Нет (CLI) | Частичный | Да | Open source + API |
| [[README|Windsurf]] | Windsurf | Да | Нет | Free + Pro |
| [[README|OpenClaw]] | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Большой монорепо, где поиск по коду важнее, чем размер окна контекста.
- Команда уже использует Sourcegraph (для code search, code insights).
- Нужно стандартизовать промпты на команду (Prompt Library).
- Enterprise-требования: self-hosted модели, приватность, audit-logs.
- **Хуже подходит, если:** маленький проект (избыточна инфраструктура), нужен мощный агент (возьмите Cursor/Claude), важны локальные модели бесплатно (Aider/OpenClaw).

## Маршруты чтения

- **Onboarding (1-2 часа):** `cody-basics.md` установка в IDE Chat Commands подключение репозитория.
- **Командное внедрение:** `cody-basics.md` + настройка Prompt Library + политики контекста на уровне организации.

## Куда идти дальше

- Сравнение всех AI-инструментов — [[README]]
- Ассистент с длинным контекстом — [[README]]
- Copilot Spaces как аналог контекстных коллекций — [[README]]
- Локальные модели через CLI — [[README]]
