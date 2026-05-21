#!/usr/bin/env bash
# verify-mcq-json.sh — валидирует MCQ-сидеры против JSON Schema.
#
# Usage:
#   bash scripts/verify-mcq-json.sh <file1.json> [file2.json ...]
#   bash scripts/verify-mcq-json.sh                            # staged files режим
#
# Требует ajv-cli (npm install -g ajv-cli) или Python jsonschema.

set -euo pipefail

SCHEMA="modules/quiz-app/src/main/resources/seed/mcq-schema.json"

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' NC='\033[0m'
else
    RED='' GRN='' NC=''
fi

declare -a FILES
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    while IFS= read -r file; do
        [[ -n "$file" ]] && FILES+=("$file")
    done < <(git diff --cached --name-only --diff-filter=AM \
        | grep -E '^modules/quiz-app/src/main/resources/seed/mcq/.*\.json$' || true)
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "verify-mcq-json: no json files to check"
    exit 0
fi

# Detect validator
if command -v ajv >/dev/null 2>&1; then
    VALIDATE_CMD=("ajv" "validate" "--spec=draft7" "--strict=false" "-s" "$SCHEMA" "-d")
elif command -v jsonschema >/dev/null 2>&1; then
    VALIDATE_CMD=("jsonschema" "-i")
else
    echo "ERROR: install 'ajv-cli' (npm i -g ajv-cli) or 'jsonschema' (pip install jsonschema)" >&2
    exit 2
fi

ERR=0
for file in "${FILES[@]}"; do
    [[ -f "$file" ]] || { echo "skip non-existent $file"; continue; }
    if "${VALIDATE_CMD[@]}" "$file" "$SCHEMA" >/dev/null 2>&1; then
        echo -e "${GRN}OK${NC}   $file"
    else
        echo -e "${RED}FAIL${NC} $file"
        "${VALIDATE_CMD[@]}" "$file" "$SCHEMA" 2>&1 | sed 's/^/    /'
        ERR=$((ERR + 1))
    fi
done

if [[ $ERR -gt 0 ]]; then
    echo -e "${RED}verify-mcq-json: $ERR error(s)${NC}"
    exit 1
fi
echo -e "${GRN}verify-mcq-json: all ${#FILES[@]} file(s) valid${NC}"
