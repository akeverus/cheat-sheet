#!/usr/bin/env bash
# Единый Python backend проверяет схему, межполевые инварианты и Markdown-пару.
# Раньше отсутствующий файл давал PASS, а ajv получал лишний аргумент.
# Без аргументов — staged paths; --all — полный корпус. Код ошибки сохраняется.
set -euo pipefail
MCQ_SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ $# -eq 0 ]]; then
    set -- --staged
fi
exec python3 "$MCQ_SCRIPT_DIR/mcq_validation.py" "$@"
