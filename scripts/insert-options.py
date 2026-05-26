#!/usr/bin/env python3
"""DEPRECATED: SQLite-based answer_options inserter.

С 2026-05-25 проект использует PostgreSQL, а MCQ-опции живут в JSON-сидерах
по пути ``modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json``.
Этот скрипт ссылался на ``modules/quiz-app/data/db/interview.db`` (SQLite),
которой больше нет, и потому при попытке запуска падал молча на коннекте.

Современный workflow:
    1. Создать/расширить JSON-сидер по схеме ``mcq-schema.json``.
    2. Запустить приложение (или INTERVIEW_RESET_ON_STARTUP=true) — McqJsonLoader
       загрузит опции в PostgreSQL автоматически.
    3. Контрактом всех сидеров занимается SeedSchemaContractTest на каждом
       ``./gradlew check``.

См. также skill ``mcq-quality-fixer`` (заменил deprecated interview-options-writer).
"""
import sys

_MSG = (
    "scripts/insert-options.py больше не поддерживается.\n"
    "MCQ-опции теперь живут в JSON-сидерах:\n"
    "  modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json\n"
    "Используй skill mcq-quality-fixer или правь JSON напрямую — на старте "
    "McqJsonLoader загрузит их в Postgres.\n"
)


def main() -> int:
    sys.stderr.write(_MSG)
    return 2


if __name__ == "__main__":
    sys.exit(main())
