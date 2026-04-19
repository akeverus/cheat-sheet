---
title: "Aider"
description: "CLI AI-pair-programmer: общение с моделью в терминале, правки через git-патчи, подключение любых LLM."
tags:
  - meta
  - index
  - tools
  - ai
  - aider
type: "index"
updated: "2026-04-17"
---
# Aider

Aider — CLI-инструмент для парного программирования с LLM: вы общаетесь с моделью в терминале, Aider редактирует файлы в репозитории, а изменения применяет через **git-патчи** — удобно для ревью. В отличие от IDE-ассистентов Aider не привязан к редактору, хорошо живёт в `tmux`/`vim`/ssh-сессиях и подходит для удалённых серверов.

Для кого: разработчики, предпочитающие терминал, сервер-сайд и удалённую работу; команды, которые хотят видеть все AI-правки как коммиты в истории; те, кто хочет быстро подключить DeepSeek, GPT, Claude, локальные модели через один интерфейс.

## Полезные ссылки

### Основные документы
- [aider-basics.md](aider-basics.md) — установка, первая сессия, работа с git, примеры промптов

### Соседние разделы
- [tools/ai/](../README.md) — родительский раздел AI-инструментов
- [tools/ai/claude/](../claude/README.md) — IDE-ассистент с длинным контекстом
- [tools/ai/cursor/](../cursor/README.md) — AI-first IDE

### Внешние ресурсы
- [Aider Documentation](https://aider.chat/)
- [Aider GitHub](https://github.com/paul-gauthier/aider)
- [Ollama](https://ollama.com/) — для локальных моделей

## Содержание

- [Что даёт Aider](#что-даёт-aider)
- [Сравнение AI-инструментов](#сравнение-ai-инструментов)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что даёт Aider

- CLI-интерфейс, минимум зависимостей, работает над ssh.
- Поддержка любых OpenAI-совместимых API и локальных моделей через Ollama.
- Каждое изменение — отдельный git-коммит с понятным сообщением.
- `/add`, `/drop`, `/diff`, `/undo` — команды управления контекстом и историей.
- Работает с архитектурными режимами (`--architect`) для сложных задач.

## Сравнение AI-инструментов

| Инструмент | Нативная IDE | Агентский режим | Локальные модели | Цена |
|------------|--------------|-----------------|------------------|------|
| [Aider](../aider/README.md) | Нет (CLI) | Частичный (architect/editor) | Да (Ollama, любые OpenAI-совместимые) | Open source + оплата провайдеру |
| [Claude Code](../claude/README.md) | VS Code, JetBrains, CLI | Да (agent, terminal-tools) | Нет (Anthropic API) | Подписка Anthropic |
| [Cody](../cody/README.md) | VS Code, JetBrains | Частичный (commands) | Enterprise: да, Cloud: нет | Free + Pro/Enterprise |
| [GitHub Copilot](../copilot/README.md) | VS Code, JetBrains, Neovim, Xcode | Да (Copilot Agent, Spaces) | Нет | Подписка GitHub |
| [Cursor](../cursor/README.md) | Cursor (форк VS Code) | Да (Agent, Background Agent) | Да (через custom API keys) | Free + Pro |
| [OpenClaw](../openclaw/README.md) | Нет (CLI) | Да (автономные агенты) | Да (Ollama, GGUF) | Open source |
| [Windsurf](../windsurf/README.md) | Windsurf (свой IDE) | Да (Cascade) | Нет | Free + Pro |

## Когда использовать

- Работаете чаще в терминале/на удалённом сервере — Aider вписывается естественно.
- Нужна понятная git-история AI-правок для ревью или аудита.
- Хотите использовать локальные модели (Ollama) без привязки к вендору.
- **Не лучший выбор, если:** нужна визуальная IDE-интеграция (возьмите Cursor/Copilot), агент с браузером (Cursor/Windsurf) или длинный контекст с IDE-awareness (Claude Code).

## Маршруты чтения

- **Первая сессия (час):** установить → `aider --model deepseek-chat` в репозитории → сделать маленькую правку → посмотреть diff.
- **Постоянный workflow:** `aider-basics.md` → настроить алиасы → подключить локальную модель через Ollama для дешёвых задач.

## Куда идти дальше

- Обзор и сравнение всех AI-инструментов — [../README.md](../README.md)
- IDE-интеграция с длинным контекстом — [../claude/README.md](../claude/README.md)
- AI-first IDE — [../cursor/README.md](../cursor/README.md), [../windsurf/README.md](../windsurf/README.md)
- Автономные агенты — [../openclaw/README.md](../openclaw/README.md)
