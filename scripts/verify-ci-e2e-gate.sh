#!/usr/bin/env bash
# verify-ci-e2e-gate.sh — гейт CS-20260828-015: три Playwright-спека
# (console-clean, reflow, keyboard-quiz) обязаны быть запущены отдельным
# job'ом в .github/workflows/ci.yml, а триггеры push/pull_request — покрывать
# правки e2e/**, иначе правка спека не запускает свой же гейт.
#
# Usage:
#   bash scripts/verify-ci-e2e-gate.sh [path/to/ci.yml]
#
# path по умолчанию — .github/workflows/ci.yml относительно корня репо,
# вычисленного от BASH_SOURCE (не от $PWD).
#
# Строки-комментарии YAML (после отбрасывания ведущих пробелов начинаются
# с '#') отбрасываются до всех проверок — упоминание спека/job'а только в
# комментарии не считается прохождением.
#
# Коды возврата:
#   0 — все инварианты выполнены, тишина на stdout
#   1 — хотя бы один инвариант нарушен; перечень нарушений на stdout

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKFLOW="${1:-$SCRIPT_DIR/.github/workflows/ci.yml}"

if [[ ! -f "$WORKFLOW" ]]; then
    echo "verify-ci-e2e-gate: workflow not found: $WORKFLOW"
    exit 1
fi

# Отбрасываем YAML-комментарии построчно: всё после '#' на строке, где '#'
# идёт первым непробельным символом. Инлайн-'#' внутри значений в этом файле
# не встречается (плоский YAML), так что построчная отбраковка безопасна.
STRIPPED="$(grep -vE '^[[:space:]]*#' "$WORKFLOW")"

ERR=0

fail() {
    echo "$1"
    ERR=$((ERR + 1))
}

if ! grep -qE '^  e2e:' <<<"$STRIPPED"; then
    fail "e2e job: not declared (expected top-level 'jobs.e2e:')"
fi

for spec in console-clean.spec.js reflow.spec.js keyboard-quiz.spec.js; do
    if ! grep -qF "$spec" <<<"$STRIPPED"; then
        fail "spec not referenced: $spec"
    fi
done

# Вырезает список paths: внутри триггера triggerKey (push|pull_request)
# блока on: — построчный YAML-скан по отступам, тот же приём что и в тесте
# CiE2eGateTest.extractTriggerPaths.
extract_trigger_paths() {
    local trigger_key="$1"
    awk -v key="$trigger_key" '
        BEGIN { inside = 0 }
        {
            line = $0
            trimmed = line
            sub(/^[ \t]+/, "", trimmed)
            indent = length(line) - length(trimmed)
            if (indent == 2 && trimmed == key ":") {
                inside = 1
                next
            }
            if (inside) {
                if (indent <= 2 && trimmed != "") {
                    inside = 0
                } else {
                    print line
                }
            }
        }
    ' <<<"$STRIPPED"
}

PUSH_PATHS="$(extract_trigger_paths push)"
if ! grep -qF "e2e/**" <<<"$PUSH_PATHS"; then
    fail "push trigger: paths do not cover e2e/**"
fi

PULL_REQUEST_PATHS="$(extract_trigger_paths pull_request)"
if ! grep -qF "e2e/**" <<<"$PULL_REQUEST_PATHS"; then
    fail "pull_request trigger: paths do not cover e2e/**"
fi

if ! grep -qE 'image:[[:space:]]*postgres:' <<<"$STRIPPED"; then
    fail "e2e job: no postgres service (expected 'image: postgres:...')"
fi

if [[ "$ERR" -gt 0 ]]; then
    exit 1
fi

exit 0
