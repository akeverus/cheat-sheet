#!/usr/bin/env bash
# verify-md-no-mcq.sh — гарантирует что .md не содержит legacy MCQ-конструкций.
# MCQ теперь живут в json-сидерах под modules/quiz-app/src/main/resources/seed/mcq/.
#
# Usage:
#   bash scripts/verify-md-no-mcq.sh <file1.md> [file2.md ...]
#   bash scripts/verify-md-no-mcq.sh                            # без аргументов: проверить staged files
#
# 0: проверенные файлы чисты или явно пустой staged-набор; 1: legacy MCQ; 2: ошибка запуска.

set -euo pipefail
MCQ_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# ──────────────── ANSI цвета (без зависимости от tput если non-tty) ──────────
if [[ -t 1 ]]; then
    RED='\033[0;31m'
    GRN='\033[0;32m'
    NC='\033[0m'
else
    RED=''
    GRN=''
    NC=''
fi

# ──────────────── Сбор списка файлов ──────────────────────────────────────────
declare -a FILES=()
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    # Проверяем ошибку Git отдельно; NUL-разделитель сохраняет пробелы и переносы в именах.
    MCQ_LIST="$(mktemp)" || exit 2
    trap 'rm -f "$MCQ_LIST"' EXIT
    git -C "$MCQ_ROOT" diff --cached --name-only -z --diff-filter=ACMR > "$MCQ_LIST" || exit 2
    while IFS= read -r -d '' line; do
        case "$line" in
            cheatsheets/interview/*.md) FILES+=("$MCQ_ROOT/$line");;
        esac
    done < "$MCQ_LIST"
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "SKIP: no staged Markdown paths; corpus was not checked"
    exit 0
fi

# ──────────────── Запрещённые паттерны (legacy MCQ конструкции) ──────────────
# Каждое правило — regex|описание для error message
declare -a RULES=(
    '^> *\[!mcq\]|> [!mcq] callout — MCQ должен быть в seed/mcq/*.json'
    '❌ ПОСЛЕДСТВИЕ:|эмодзи-маркер старого формата'
    '✓ ПРИМЕНЯТЬ:|эмодзи-маркер старого формата'
    '📋 ПРАВИЛО:|эмодзи-маркер старого формата'
    '🔗 См\. Q[0-9]|эмодзи-маркер старого формата'
    '\[\[Q[0-9]+\]\]|legacy [[Q<N>]] — используйте [[<file>#Q<N>]]'
)

# ──────────────── Проход по файлам ────────────────────────────────────────────
ERR=0
for file in "${FILES[@]}"; do
    if [[ ! -f "$file" ]]; then
        echo "ERROR: Markdown file not found: $file" >&2
        exit 2
    fi

    file_err=0
    for rule in "${RULES[@]}"; do
        regex="${rule%%|*}"
        desc="${rule#*|}"
        if matches=$(grep -nE -- "$regex" "$file"); then
            :
        else
            MCQ_GREP_EXIT=$?
            if [[ $MCQ_GREP_EXIT -ne 1 ]]; then
                echo "ERROR: Markdown scan failed: $file" >&2
                exit 2
            fi
        fi
        if [[ -n "$matches" ]]; then
            echo -e "${RED}FAIL${NC} $file: $desc"
            echo "$matches" | head -3 | sed 's/^/    /'
            file_err=$((file_err + 1))
        fi
    done

    if [[ $file_err -eq 0 ]]; then
        echo -e "${GRN}OK${NC}   $file"
    else
        ERR=$((ERR + file_err))
    fi
done

# ──────────────── Итог ────────────────────────────────────────────────────────
if [[ $ERR -gt 0 ]]; then
    echo -e "${RED}verify-md-no-mcq: $ERR error(s)${NC}"
    exit 1
fi
echo -e "${GRN}verify-md-no-mcq: all clean${NC}"
