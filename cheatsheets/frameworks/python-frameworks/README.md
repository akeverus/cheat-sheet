---
title: "Python Frameworks"
description: "Точка входа в раздел Python-фреймворков: Django, FastAPI, Flask, Starlette, ASGI/WSGI, ORM и практики."
tags:
  - meta
  - index
  - python-frameworks
type: "index"
updated: "2026-04-17"
---
# Python Frameworks

Раздел собирает основные веб- и прикладные фреймворки Python: Django (батарейки включены, админка, ORM), FastAPI (async REST с Pydantic), Flask (минимализм), Starlette (ASGI-основа FastAPI). Сопутствующие темы — ASGI/WSGI, SQLAlchemy/Tortoise/Django ORM, тестирование (pytest) и развёртывание (gunicorn/uvicorn, Docker).

Для кого: Python-разработчики, выбирающие backend-стек, и инженеры из других экосистем, оценивающие FastAPI как "Spring-killer для прототипов" или Django как all-in-one платформу.

## Полезные ссылки

### Основные документы
- [Python Frameworks — обзор](python-frameworks-overview.md) — Django, FastAPI, Flask, Starlette, ORM, сравнение

### Соседние разделы
- [Frameworks](../README.md)
- [Java Frameworks](../java-frameworks/README.md) — для сравнения со Spring/Quarkus

### Внешние ресурсы
- [Django Documentation](https://docs.djangoproject.com/)
- [FastAPI](https://fastapi.tiangolo.com/)
- [Flask](https://flask.palletsprojects.com/)
- [Starlette](https://www.starlette.io/)
- [SQLAlchemy](https://www.sqlalchemy.org/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Python](#когда-выбирать-python)
- [Сравнение фреймворков](#сравнение-фреймворков)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Django: MTV, admin, ORM, DRF
- FastAPI: async, Pydantic, автогенерация OpenAPI
- Flask: микрофреймворк с extensions
- Starlette: ASGI-ядро
- ORM: SQLAlchemy (Core + ORM), Django ORM, Tortoise
- Развёртывание: gunicorn, uvicorn, hypercorn

## Когда выбирать Python

- Нужен быстрый прототип REST-сервиса с автодокументацией → **FastAPI**.
- Нужна CMS/админка/CRUD «из коробки» → **Django**.
- Нужен минимум кода и полная свобода → **Flask**.
- Альтернативы вне Python: Node.js (Express/Fastify), Go (Gin), Spring Boot (JVM) — если важна производительность JIT и строгая типизация runtime.

## Сравнение фреймворков

| Фреймворк | Ниша | Async | ORM | Автодокументация |
|-----------|------|-------|-----|------------------|
| Django | full-stack monolith | частично (ASGI с 4.1) | Django ORM | через DRF + drf-spectacular |
| FastAPI | async REST API | да (нативно) | SQLAlchemy 2 / Tortoise / SQLModel | из коробки (OpenAPI/Swagger) |
| Flask | микро-REST | нет (sync) | SQLAlchemy + Flask-SQLAlchemy | через flasgger / apispec |
| Starlette | ASGI-ядро | да (нативно) | любой | нет (низкоуровневый) |

## Маршруты чтения

- **Прототип API за час:** `python-frameworks-overview.md` → раздел FastAPI.
- **Full-stack с админкой:** раздел Django + ORM.
- **Выбор фреймворка:** таблица сравнения + раздел «Сравнение фреймворков» в overview.

## Куда идти дальше

- Общая разработка backend — [development/web-backend](../../development/web-backend/README.md)
- Базы данных — [databases](../../databases/README.md)
- Docker-деплой — [platform/containers/docker](../../platform/containers/docker/README.md)
