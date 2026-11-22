---
title: "Вопросы на собеседовании: Упаковка Python-приложений"
description: "pyproject.toml, wheel и sdist, зависимости, повторяемая установка и код при запуске Python"
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
updated: "2026-11-02"
---
# Вопросы на собеседовании: Упаковка Python-приложений

Ориентир — спецификации PyPA, документация pip и Python 3.15, проверенные 2026-11-02. Возможности установки групп и lock-файлов зависят от выбранного инструмента и его версии; сам формат проекта не фиксирует среду развёртывания.

## Полезные ссылки

- [Спецификация `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/) — сборка и metadata.
- [Package formats](https://packaging.python.org/en/latest/discussions/package-formats/) — wheel и sdist.
- [Dependency Groups](https://packaging.python.org/en/latest/specifications/dependency-groups/) — зависимости разработки.
- [Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/) — pinning, hashes и wheelhouse.
- [Модуль `site`](https://docs.python.org/3.15/library/site.html) — код пакетов до запуска приложения.

## Содержание

- [Q1. Что разделяют build-system, project и tool в pyproject.toml?](#q1-что-разделяют-build-system-project-и-tool-в-pyprojecttoml)
- [Q2. Чем wheel отличается от sdist при установке сервиса?](#q2-чем-wheel-отличается-от-sdist-при-установке-сервиса)
- [Q3. Чем зависимости проекта, extras и dependency groups отличаются друг от друга?](#q3-чем-зависимости-проекта-extras-и-dependency-groups-отличаются-друг-от-друга)
- [Q4. Как сделать установку зависимостей повторяемой в CI и deployment?](#q4-как-сделать-установку-зависимостей-повторяемой-в-ci-и-deployment)
- [Q5. Что делают .pth и .start при запуске Python 3.15?](#q5-что-делают-pth-и-start-при-запуске-python-315)
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

**Сценарий.** После перехода с Linux-образа на glibc на образ с musl один из пакетов с нативным расширением доступен только как sdist для выбранной версии. Чтобы выявить это в CI на целевом образе, проверьте установку в чистом окружении из каталога с подготовленными дистрибутивами. В примере `requirements.lock` содержит закреплённые имена и версии без прямых ссылок на архивы.

```bash
python -m pip install --no-index --find-links ./wheelhouse \
    --only-binary=:all: -r requirements.lock
```

Если в каталоге нет подходящего wheel для обязательной зависимости, установка завершится ошибкой вместо сборки sdist. Тогда можно подготовить wheel для целевого окружения отдельным шагом CI или выбрать совместимую версию зависимости. `--prefer-binary` оставляет установку из исходников допустимой. Проверки хэшей и среды сборки описаны в Q4. [Опции pip install](https://pip.pypa.io/en/stable/cli/pip_install/#options).

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

**Пример установки в CI.** Из каталога с этим `pyproject.toml` команда `python -m pip install --group test` (pip 25.1+) установит требования группы. Она сама по себе не устанавливает `orders-api` и его runtime-зависимости. Если тесты импортируют установленный пакет, запустите `python -m pip install . --group test`, чтобы передать resolver и проект, и тестовую группу за один вызов. Для тестов с PostgreSQL вместо `.` укажите `".[postgres]"`. Ограничение `pytest>=8` оставляет установщику выбор версии. Для воспроизводимой установки используйте подход из Q4. [pip — Dependency Groups](https://pip.pypa.io/en/stable/user_guide/#dependency-groups), [PyPA — Dependency Groups](https://packaging.python.org/en/latest/specifications/dependency-groups/).

## Q4. Как сделать установку зависимостей повторяемой в CI и deployment?

Диапазоны версий в `pyproject.toml` описывают совместимость, но не фиксируют выбранное **полное дерево** зависимостей. Для повторяемой установки фиксируют прямые и транзитивные версии в файле, поддерживаемом installer, и воспроизводят его в чистой среде с оговорёнными версией Python и платформой. Для pip requirements можно добавить hashes архивов; это проверяет содержимое загружаемых файлов, а не только номера релизов. В режиме pip `--require-hashes` **каждая** прямая и транзитивная зависимость должна быть явно закреплена и иметь hash подходящего архива: частично заполненный файл не пройдёт установку. [pip Secure Installs](https://pip.pypa.io/en/stable/topics/secure-installs/#hash-checking-mode).

Если сборка из sdist недопустима на сервере, заранее собирайте или сохраняйте подходящие wheels и устанавливайте из контролируемого wheelhouse. Он привязан к совместимым ОС, архитектуре и ABI. `pylock.toml` стандартизует формат lock-файла (PEP 751), но наличие стандарта не означает поддержку каждым installer: проверяйте инструмент и CI-команду. Ни pinning, ни hashes сами по себе не делают сборку всего приложения побитово воспроизводимой — важны входные артефакты, build backend и окружение.

**Источник артефактов.** Если у сервиса есть внутренние зависимости, задайте политику источников и для разрешения версий, и для установки. В pip `--extra-index-url` добавляет кандидатов из второго индекса без приоритета основного. Для разрешения используйте контролируемый индекс, который отдельно обрабатывает внутренние имена и пропускает только разрешённые публичные пакеты. Хэши фиксируйте после проверки происхождения артефактов.

Для установки в чистый образ из заранее подготовленного wheelhouse используйте локальный файл требований с закреплёнными версиями и хэшами всех нужных wheels:

```bash
python -m pip install --no-index --find-links=wheelhouse \
    --only-binary=:all: --require-hashes -r requirements.txt
```

Для этого сценария файл требований должен содержать имена, версии и хэши без внешних URL. `--no-index` отключает поиск по индексам, а ограничение сетевого доступа обеспечивает окружение CI или deployment. Если подходящего wheel с разрешённым хэшем нет, установка завершится ошибкой. [pip install — поиск пакетов](https://pip.pypa.io/en/stable/cli/pip_install/#finding-packages).

**Хэш исходника и хэш собранного wheel.** Начиная с pip 23.1 режим `--require-hashes` может использовать локальный кеш wheel, собранных pip. В таком случае pip сверяет хэш исходного архива по метаданным `origin.json` записи кеша. Если CI переносит собранный `.whl` в wheelhouse как отдельный артефакт, для установки из этого каталога закрепите хэш самого wheel. Хэш sdist из файла для сборки не подходит для установки этого wheel. Например, вычислите его командой `python -m pip hash wheelhouse/orders_api-1.0.0-py3-none-any.whl` и запишите в файл требований для deployment вместе с хэшами остальных дистрибутивов целевого образа. [pip: кеш и проверка хэшей](https://pip.pypa.io/en/stable/topics/secure-installs/#interaction-with-caching), [pip hash](https://pip.pypa.io/en/stable/cli/pip_hash/).

**Если CI собирает wheel из исходников.** Даже с фиксированным набором runtime-зависимостей сборка может начать падать после выхода новой версии setuptools или Cython. При изолированной сборке pip разрешает зависимости в отдельном окружении с учётом требований backend. В pip 25.3+ их можно ограничить отдельным файлом. С pip 26.2 обычные constraints, включая `PIP_CONSTRAINT`, больше не влияют на изолированную среду сборки.

```text
# build-constraints.txt: пример версии, проверенной с этим проектом
setuptools==80.9.0
```

```bash
python -m pip wheel --build-constraint build-constraints.txt --wheel-dir wheelhouse .
```

Ограничение применяется, если setuptools нужен для сборки, и должно быть совместимо с требованиями проекта. Оно не добавляет setuptools в зависимости приложения и не фиксирует системные библиотеки или компилятор. В deployment устанавливают проверенные wheels из wheelhouse, поэтому повторная сборка там не нужна. [pip Build Constraints](https://pip.pypa.io/en/stable/user_guide/#build-constraints), [pip wheel](https://pip.pypa.io/en/stable/cli/pip_wheel/).

**Итог:** храните исходные ограничения отдельно от проверенного набора артефактов развёртывания и тестируйте установку в целевом образе. [pip Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/), [`pylock.toml` specification](https://packaging.python.org/en/latest/specifications/pylock-toml/).

## Q5. Что делают `.pth` и `.start` при запуске Python 3.15?

При обычном запуске модуль `site` обрабатывает файлы в `site-packages` **до первой строки приложения**. Строки с путями в `name.pth` расширяют `sys.path`. Строки, начинающиеся с `import`, исторически исполняют код; в Python 3.15 они устарели, но пока выполняются, если рядом нет `name.start` с тем же именем. При наличии парного `.start` такие `import`-строки игнорируются, а строки с путями в `.pth` продолжают работать.

Новый `name.start` (Python 3.15+) перечисляет точки входа вида `pkg.mod:callable`. `site` сначала применяет расширения путей из `.pth`, затем вызывает каждую функцию без аргументов. Вызов происходит при старте интерпретатора, даже если приложение не импортирует пакет. Например, строка `telemetry.bootstrap:initialize` в `telemetry.start` вызовет `initialize()` до входа в код сервиса. Одинаковые точки входа не удаляются как дубли и вызываются повторно; ошибка одного вызова печатается в `stderr`, после чего обрабатываются остальные. Поэтому startup hook должен учитывать повторный вызов и не рассчитывать, что его ошибка остановит запуск. [Документация `site`](https://docs.python.org/3.15/library/site.html#startup-entry-points-start-files).

При разборе неожиданного кода или задержки старта проверяйте установленные `.pth` и `.start` в целевом окружении вместе с зависимостями из Q4. `.start` делает точку запуска явной, но не изолирует исполняемый код. Опция `-S` отключает автоматический импорт `site` и эту обработку, одновременно меняя обычную настройку путей поиска модулей; это диагностическая граница, а не безусловное исправление. [Документация `site`](https://docs.python.org/3.15/library/site.html), [PEP 829](https://peps.python.org/pep-0829/).

---

## See also

- [Основы Python](python-interview.md) — модель языка и зависимости во время выполнения.
- [Тестирование в Python](python-testing-interview.md) — изоляция инструментов CI.
- [Стратегии развёртывания](../../cicd/deployment-strategies-interview.md) — доставка проверенного артефакта.
