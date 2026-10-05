---
title: "Вопросы на собеседовании: Упаковка Python-приложений"
description: "pyproject.toml, wheel и sdist, классы зависимостей и повторяемая установка для Python backend"
tags:
  - interview
  - programming-languages
  - python-packaging-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python packaging interview"
  - "Упаковка Python на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: Упаковка Python-приложений

Ориентир — спецификации PyPA и документация pip, проверенные 2026-10-05. Возможности установки групп и lock-файлов зависят от выбранного инструмента и его версии; сам формат проекта не фиксирует среду развёртывания.

## Полезные ссылки

- [Спецификация `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/) — сборка и metadata.
- [Package formats](https://packaging.python.org/en/latest/discussions/package-formats/) — wheel и sdist.
- [Dependency Groups](https://packaging.python.org/en/latest/specifications/dependency-groups/) — зависимости разработки.
- [Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/) — pinning, hashes и wheelhouse.

## Содержание

- [Q1. Что разделяют build-system, project и tool в pyproject.toml?](#q1-что-разделяют-build-system-project-и-tool-в-pyprojecttoml)
- [Q2. Чем wheel отличается от sdist при установке сервиса?](#q2-чем-wheel-отличается-от-sdist-при-установке-сервиса)
- [Q3. Чем зависимости проекта, extras и dependency groups отличаются друг от друга?](#q3-чем-зависимости-проекта-extras-и-dependency-groups-отличаются-друг-от-друга)
- [Q4. Как сделать установку зависимостей повторяемой в CI и deployment?](#q4-как-сделать-установку-зависимостей-повторяемой-в-ci-и-deployment)
- [See also](#see-also)

## Q1. Что разделяют build-system, project и tool в pyproject.toml?

`[build-system]` объявляет зависимости и backend **сборки** (`requires`, `build-backend`). `[project]` описывает metadata выпускаемого проекта: имя, версию или `dynamic`, поддерживаемый Python, runtime-зависимости, scripts. `[tool.<имя>]` хранит настройки конкретного инструмента, например тестового runner или formatter. Это разные контракты: build-зависимость не становится автоматически runtime-зависимостью сервиса.

```toml
[build-system]
requires = ["setuptools>=77"]
build-backend = "setuptools.build_meta"

[project]
name = "orders-api"
version = "1.0.0"
requires-python = ">=3.11"
dependencies = ["httpx>=0.27"]
```

Версия здесь иллюстративна, а поддержка конкретного backend и его параметров проверяется по его документации. `pyproject.toml` не является lock-файлом: диапазон `httpx>=0.27` допускает выбор разных релизов при разрешении зависимостей. Для старых проектов `setup.py` или `setup.cfg` могут оставаться действующими; формат не нужно менять только ради моды. [Спецификация `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/), [руководство PyPA](https://packaging.python.org/en/latest/guides/writing-pyproject-toml/).

## Q2. Чем wheel отличается от sdist при установке сервиса?

**Wheel** — собранный дистрибутив для установки; при нативных расширениях он может содержать скомпилированные файлы и иметь ограничения по Python ABI, ОС и архитектуре. **Sdist** — исходный архив с metadata; если подходящего wheel нет, pip может собрать wheel из sdist и затем установить его. Для этого нужны совместимый build backend и иногда системные компиляторы/библиотеки.

При развертывании сервиса проверяйте, есть ли wheel под целевой образ и версию Python. Переход с одного базового Linux-образа или архитектуры на другой может изменить набор подходящих wheels и привести к сборке из исходников. Сборка в CI и установка проверенного wheel уменьшают неожиданности на production-хосте; сам wheel всё равно требует проверки зависимостей и совместимости платформы. Универсальный pure-Python wheel может подходить многим платформам, но не всякий wheel универсален. [Package formats](https://packaging.python.org/en/latest/discussions/package-formats/), [platform compatibility tags](https://packaging.python.org/en/latest/specifications/platform-compatibility-tags/).

## Q3. Чем зависимости проекта, extras и dependency groups отличаются друг от друга?

`[project].dependencies` попадают в metadata пакета как обычные runtime-зависимости. `[project.optional-dependencies]` задаёт **extras** для потребителя пакета, например `orders-api[postgres]`: они также входят в metadata и выбираются при установке. `[dependency-groups]` предназначены для локальных задач вроде тестов или линтера; данные групп **не входят в metadata собранного дистрибутива**.

```toml
[project]
name = "orders-api"
version = "1.0.0"
dependencies = ["httpx>=0.27"]

[project.optional-dependencies]
postgres = ["psycopg>=3"]

[dependency-groups]
test = ["pytest>=8"]
```

Не переносите тестовые инструменты в runtime-зависимости только чтобы установить их в CI. Спецификация групп не задаёт единой команды установки: проверьте поддержку и синтаксис выбранного installer. Extras подходят, когда дополнительную функциональность пакета выбирает его пользователь; группа — когда зависимости нужны процессу разработки проекта. [Спецификация `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/#optional-dependencies), [Dependency Groups](https://packaging.python.org/en/latest/specifications/dependency-groups/).

## Q4. Как сделать установку зависимостей повторяемой в CI и deployment?

Диапазоны версий в `pyproject.toml` описывают совместимость, но не фиксируют выбранное **полное дерево** зависимостей. Для повторяемой установки фиксируют прямые и транзитивные версии в файле, поддерживаемом installer, и воспроизводят его в чистой среде с оговорёнными версией Python и платформой. Для pip requirements можно добавить hashes архивов; это проверяет содержимое загружаемых файлов, а не только номера релизов.

Если сборка из sdist недопустима на сервере, заранее собирайте или сохраняйте подходящие wheels и устанавливайте из контролируемого wheelhouse. Он привязан к совместимым ОС, архитектуре и ABI. `pylock.toml` стандартизует формат lock-файла (PEP 751), но наличие стандарта не означает поддержку каждым installer: проверяйте инструмент и CI-команду. Ни pinning, ни hashes сами по себе не делают сборку всего приложения побитово воспроизводимой — важны входные артефакты, build backend и окружение.

**Итог:** храните исходные ограничения отдельно от проверенного набора артефактов развёртывания и тестируйте установку в целевом образе. [pip Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/), [`pylock.toml` specification](https://packaging.python.org/en/latest/specifications/pylock-toml/).

---

## See also

- [Основы Python](python-interview.md) — модель языка и зависимости во время выполнения.
- [Тестирование в Python](python-testing-interview.md) — изоляция инструментов CI.
- [Стратегии развёртывания](../../cicd/deployment-strategies-interview.md) — доставка проверенного артефакта.
