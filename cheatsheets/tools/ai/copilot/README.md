---
title: "GitHub Copilot"
description: "AI-ассистент GitHub в IDE: inline suggestions, Copilot Chat, Copilot Spaces, agents и repository custom instructions."
tags:
  - meta
  - index
  - tools
  - ai
  - copilot
type: "index"
updated: "2026-04-20"
---
# GitHub Copilot

GitHub Copilot — один из самых распространённых AI-ассистентов: inline-подсказки в редакторе, Copilot Chat для обсуждения, Copilot Spaces (собственные коллекции контекста), Copilot Agent для автономных задач и интеграция с GitHub (PR-ревью, issue-суммаризация). Поддерживает выбор модели (GPT, Claude, Gemini) в зависимости от плана.

Для кого: команды, уже живущие в экосистеме GitHub; разработчики, которым нужна простая подписка без возни с API-ключами; компании, где критичен enterprise-биллинг и compliance через GitHub.

## Полезные ссылки

### Основные документы
- [[copilot-basics]] — установка, inline suggestions, Chat, Spaces, custom instructions

### Соседние разделы
- [[README|tools/ai/]] — родительский раздел
- [[README|tools/ai/claude/]] — альтернатива с длинным контекстом
- [[README|tools/ai/cody/]] — альтернатива с code graph
- [devops/git/](../../../devops/git/README.md) — общий раздел по Git

### Внешние ресурсы
- [GitHub Copilot Docs](https://docs.github.com/en/copilot)
- [Copilot Chat в IDE](https://docs.github.com/en/copilot/how-tos/use-chat/use-chat-in-ide)
- [Copilot Spaces](https://docs.github.com/en/copilot/how-tos/provide-context/use-copilot-spaces/use-copilot-spaces)
- [Repository custom instructions](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)

## Содержание

- [Ключевые возможности](#ключевые-возможности)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Ключевые возможности

- Inline suggestions — быстрые подсказки по мере набора.
- Copilot Chat — чат в IDE с контекстом открытых файлов/проекта.
- Copilot Spaces — коллекции файлов и документов для переиспользуемого контекста.
- Copilot Agent — автономное решение задач в репозитории.
- Custom instructions (`.github/copilot-instructions.md`) — инструкции на уровне репо.
- CLI (`gh copilot`) — подсказки команд в терминале.
- PR-ревью и issue-суммаризация на GitHub.com.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [[README|GitHub Copilot]] | VS Code, JetBrains, Neovim, Xcode, Visual Studio | Да (Copilot Agent, Spaces) | Нет | Подписка GitHub (Individual/Business/Enterprise) |
| [[README|Claude Code]] | VS Code, JetBrains, CLI | Да | Нет | Подписка Anthropic |
| [[README|Cursor]] | Cursor | Да | Да (custom API) | Free + Pro |
| [[README|Cody]] | VS Code, JetBrains | Частичный | Enterprise | Free + Pro/Enterprise |
| [[README|Windsurf]] | Windsurf | Да | Нет | Free + Pro |
| [[README|Aider]] | Нет (CLI) | Частичный | Да | Open source + API |
| [[README|OpenClaw]] | Нет (CLI) | Да | Да | Open source |

## Когда использовать

- Команда уже в GitHub — единый биллинг, доступ через организацию.
- Нужна проверенная экосистема (IDE-поддержка, плагины, community).
- Compliance — Business/Enterprise-тарифы дают data retention opt-out и audit logs.
- **Хуже подходит, если:** нужны локальные модели (Aider/OpenClaw), максимальный контроль контекста и code graph (Cody), самый длинный контекст (Claude).

## Маршруты чтения

- **Первый день:** `copilot-basics.md` установка в IDE inline Chat.
- **Команда:** + custom instructions в репо + Spaces для shared-контекстов + Copilot Agent для рутинных задач.
- **Автоматизация:** `gh copilot` в CLI + GitHub Actions с Copilot-интеграцией.

## Куда идти дальше

- Сравнение и выбор — [[README]]
- Длинный контекст — [[README]]
- AI-first IDE — [[README]]
- Код-граф и монорепо — [[README]]
