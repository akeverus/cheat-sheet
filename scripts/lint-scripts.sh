#!/usr/bin/env bash
# lint-scripts.sh — мета-гейт целостности тулбелта: синтаксическая проверка
# всех scripts/*.sh и scripts/**/*.py.
#
# Usage:
#   bash scripts/lint-scripts.sh [target_dir]
#
# target_dir по умолчанию — каталог самого скрипта (обычно scripts/), не $PWD.
#
# Коды возврата:
#   0 — все найденные файлы валидны
#   1 — есть синтаксические ошибки, либо target_dir не существует / файлов не найдено
#   2 — найдены *.py, но python3 не установлен

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET="${1:-$SCRIPT_DIR}"

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' NC='\033[0m'
else
    RED='' GRN='' NC=''
fi

if [[ ! -d "$TARGET" ]]; then
    echo "lint-scripts: target directory not found: $TARGET" >&2
    exit 1
fi

declare -a FILES
HAS_PY=0
while IFS= read -r -d '' f; do
    FILES+=("$f")
    case "$f" in
        *.py) HAS_PY=1 ;;
    esac
done < <(find "$TARGET" -name __pycache__ -prune -o -type f \( -name '*.sh' -o -name '*.py' \) -print0 | sort -z)

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "lint-scripts: no *.sh/*.py files found under $TARGET" >&2
    exit 1
fi

if [[ "$HAS_PY" -eq 1 ]] && ! command -v python3 >/dev/null 2>&1; then
    echo "lint-scripts: python3 not found in PATH, but *.py files were found" >&2
    exit 2
fi

PYCACHE_DIR=""
if [[ "$HAS_PY" -eq 1 ]]; then
    PYCACHE_DIR="$(mktemp -d)"
    trap 'rm -rf "$PYCACHE_DIR"' EXIT
fi

ERR=0
for f in "${FILES[@]}"; do
    case "$f" in
        *.sh)
            if ! out=$(bash -n "$f" 2>&1); then
                echo -e "${RED}FAIL${NC} $f"
                echo "$out" | sed 's/^/    /'
                ERR=$((ERR + 1))
            fi
            ;;
        *.py)
            if ! out=$(PYTHONPYCACHEPREFIX="$PYCACHE_DIR" python3 -m py_compile "$f" 2>&1); then
                echo -e "${RED}FAIL${NC} $f"
                echo "$out" | sed 's/^/    /'
                ERR=$((ERR + 1))
            fi
            ;;
    esac
done

if [[ "$ERR" -gt 0 ]]; then
    echo -e "${RED}lint-scripts: $ERR error(s)${NC}"
    exit 1
fi

if [[ -t 1 ]]; then
    echo -e "${GRN}lint-scripts: ${#FILES[@]} file(s) OK${NC}"
fi
exit 0
