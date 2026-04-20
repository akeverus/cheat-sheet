---
title: "Python Frameworks — обзор"
description: "Кратко: обзор веб- и прикладных фреймворков Python: Django, FastAPI, Flask, Starlette, ASGI/ WSGI, ORM, асинхронность и практики."
tags:
  - frameworks
  - python-frameworks
  - python-frameworks-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Python Frameworks — обзор

Кратко: обзор веб- и прикладных фреймворков **Python**: **Django**, **FastAPI**, **Flask**, **Starlette**, **ASGI**/ **WSGI**, **ORM**, асинхронность и практики.

## Полезные ссылки

### Официальная документация
- [Django](https://docs.djangoproject.com/)
- [FastAPI](https://fastapi.tiangolo.com/)
- [Flask](https://flask.palletsprojects.com/)

### См. также
- [[README|Frameworks README]] — раздел фреймворков
- [[README|Java Frameworks]] — **Spring**, **Quarkus**

## Содержание

- [Введение](#введение)
- [Django](#django)
- [FastAPI](#fastapi)
- [Flask](#flask)
- [Starlette и ASGI](#starlette-и-asgi)
- [Сравнение фреймворков](#сравнение-фреймворков)
- [ORM и базы данных](#orm-и-базы-данных)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)


## Введение

**Python** широко используется для веб-приложений и **API**. **Django** — полноценный фреймворк с **ORM**, админкой, шаблонами. **FastAPI** — современный асинхронный фреймворк для **API** с автоматической документацией **OpenAPI**. **Flask** — микрофреймворк с гибкостью и расширениями. Документ охватывает установку, примеры и рекомендации.

**Ключевые понятия:** **WSGI**, **ASGI**, **ORM**, **Pydantic**, **OpenAPI**, **middleware**.


## Django

**Django** — «батарейки включены»: **ORM**, миграции, админ-панель, аутентификация, формы, шаблоны.

**Установка и запуск:**
```bash
# Установка Django и создание проекта
pip install django
django-admin startproject myproject
cd myproject
python manage.py startapp api
python manage.py runserver
```

**Пример view (API):**
```python
# View для API с JsonResponse
from django.http import JsonResponse
def item_detail(request, id):
    return JsonResponse({"id": id})
# urls.py: path('api/items/<int:id>/', item_detail),
```

**DRF (Django REST Framework):** для **REST API** с сериализаторами, **ViewSet**, аутентификацией. Стандарт для **API** поверх **Django**.


## FastAPI

**FastAPI** — асинхронный фреймворк на **Starlette** и **Pydantic**: типизация, автоматическая **OpenAPI** документация, валидация из коробки.

**Установка:**
```bash
# Установка FastAPI и ASGI-сервера uvicorn
pip install fastapi uvicorn
```

**Пример:**
```python
# Минимальное приложение FastAPI с эндпоинтами
from fastapi import FastAPI
app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "Hello"}

@app.get("/api/items/{item_id}")
def read_item(item_id: int):
    return {"item_id": item_id}

@app.post("/api/items")
def create_item(name: str):
    return {"name": name}
```

**Запуск:** `uvicorn main:app --reload`. Документация: `/docs` (Swagger), `/redoc`.


## Flask

**Flask** — микрофреймворк: маршрутизация, **Jinja2**, расширения для **ORM**, **REST**, аутентификации.

**Пример:**
```python
# Минимальное приложение Flask с JSON
from flask import Flask, jsonify
app = Flask(__name__)

@app.route("/")
def index():
    return jsonify({"message": "Hello"})

@app.route("/api/items/<int:id>")
def get_item(id):
    return jsonify({"id": id})
```

**WSGI**-сервер: **Gunicorn**, **uWSGI**. Для асинхронности — расширения или переход на **FastAPI**/ **Starlette**.


## Starlette и ASGI

**ASGI** (Asynchronous Server Gateway Interface) — асинхронный стандарт. **Starlette** — легковесный **ASGI** toolkit; **FastAPI** построен на нём. **Django** с **Django Channels** поддерживает **ASGI**. Для высоконагруженных асинхронных **API** типичен **FastAPI** + **Uvicorn**.


## Сравнение фреймворков

| Фреймворк | Тип | Асинхронность | ORM/админка | Документация API |
|-----------|-----|---------------|-------------|------------------|
| **Django** | Полный | Опционально (Channels) | Да, встроенная | Через DRF |
| **FastAPI** | API-first | Да | Нет (подключить отдельно) | OpenAPI из коробки |
| **Flask** | Микро | Нет (расширения) | Расширения | Расширения |

**Выбор:** **Django** — большие проекты, админка, **ORM**; **FastAPI** — современный **API**, типизация, **OpenAPI**; **Flask** — гибкость, маленькие приложения.


## ORM и базы данных

**Django ORM** — встроенный, миграции, модели. **SQLAlchemy** — универсальная **ORM**, используется с **Flask** и **FastAPI**. **Tortoise-ORM** — асинхронная **ORM** для **FastAPI**. **Alembic** — миграции для **SQLAlchemy**.


## Лучшие практики

- **FastAPI**: использовать **Pydantic** модели для запросов и ответов; зависимости через `Depends()`.
- **Django**: приложения разбивать по доменам; **DRF** для **API**; миграции в **CI**.
- **Flask**: структура приложения (blueprints), конфигурация из окружения, **Gunicorn** в production.
- Секреты не в коде; переменные окружения или менеджеры секретов.


## Решение проблем

| Проблема | Действие |
|----------|----------|
| **Django** миграции конфликт | Проверить порядок, разрешить конфликты в файлах миграций |
| **FastAPI** 422 | Проверить типы и **Pydantic** модели (query/body/path) |
| **Flask** 404 | Проверить порядок правил и регистрацию blueprints |


## Частые вопросы

**Django или FastAPI?** **Django** — полноценный веб-стек и админка. **FastAPI** — быстрый **API**, асинхронность, **OpenAPI**. Часто комбинируют: админка на **Django**, публичный **API** на **FastAPI**.

**WSGI vs ASGI?** **WSGI** — синхронный стандарт (Gunicorn, uWSGI). **ASGI** — асинхронный (Uvicorn, Daphne). Новые проекты на **FastAPI** используют **ASGI**.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **WSGI** | Web Server Gateway Interface — стандарт синхронного взаимодействия сервера и приложения |
| **ASGI** | Asynchronous Server Gateway Interface — асинхронный стандарт |
| **Pydantic** | Библиотека валидации и сериализации на типах |
| **DRF** | Django REST Framework |
| **Starlette** | Легковесный **ASGI** toolkit |
| **Uvicorn** | **ASGI** сервер |


## Заключение

**Python**-экосистема предлагает **Django** для полноценных приложений, **FastAPI** для современного **API**, **Flask** для гибкости. Выбор по требованиям к **ORM**, админке и асинхронности. См. [[README|Frameworks README]].


