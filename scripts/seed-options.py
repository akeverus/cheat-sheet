#!/usr/bin/env python3
"""DEPRECATED: SQLite-based offline option seeder via Claude.

С 2026-05-25 проект использует PostgreSQL, а MCQ-опции живут в JSON-сидерах
по пути ``modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json``.
Этот скрипт ссылался на ``modules/quiz-app/data/db/interview.db`` (SQLite),
которой больше нет — sqlite3.connect падал на пустой путь.

Современный workflow:
    1. Используй skill ``mcq-quality-fixer`` (заменил deprecated
       ``interview-options-writer``) — он генерирует/правит JSON-сидеры.
    2. JSON попадает в БД на старте приложения через ``McqJsonLoader``;
       для перезаливки — ``INTERVIEW_RESET_ON_STARTUP=true``.
    3. Контракт всех сидеров — ``SeedSchemaContractTest`` на
       ``./gradlew check``.

Если нужна batch-генерация через Claude — пиши JSON-сидеры напрямую
из skill'а, потом ``./gradlew bootRun`` сам загрузит.
"""
import sys

_MSG = (
    "scripts/seed-options.py больше не поддерживается.\n"
    "MCQ-опции теперь генерируются в JSON-сидеры:\n"
    "  modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json\n"
    "Используй skill mcq-quality-fixer; на старте приложения McqJsonLoader\n"
    "загрузит сидеры в PostgreSQL автоматически.\n"
)


def main() -> int:
    sys.stderr.write(_MSG)
    return 2


if __name__ == "__main__":
    sys.exit(main())
