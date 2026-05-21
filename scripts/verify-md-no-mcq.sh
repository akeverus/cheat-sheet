#!/usr/bin/env bash
# verify-md-no-mcq.sh — гарантирует что .md не содержит legacy MCQ-конструкций.
# MCQ теперь живут в json-сидерах под modules/quiz-app/src/main/resources/seed/mcq/.
#
# Usage:
#   bash scripts/verify-md-no-mcq.sh <file1.md> [file2.md ...]
#   bash scripts/verify-md-no-mcq.sh                            # без аргументов: проверить staged files
#
# Возвращает 0 если все проверки прошли, 1 при обнаружении legacy MCQ-конструкций.

set -euo pipefail

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
declare -a FILES
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    # Staged files mode (для pre-commit hook) — portable version for bash 3.2
    while IFS= read -r line; do
        [[ -z "$line" ]] && continue
        FILES+=("$line")
    done < <(git diff --cached --name-only --diff-filter=AM | grep -E '^cheatsheets/interview/.*\.md$' || true)
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "verify-md-no-mcq: no md files to check"
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
        continue
    fi

    file_err=0
    for rule in "${RULES[@]}"; do
        regex="${rule%%|*}"
        desc="${rule#*|}"
        matches=$(grep -nE "$regex" "$file" 2>/dev/null || true)
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
