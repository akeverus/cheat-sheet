#!/usr/bin/env bash
# Stop hook: проверяет, не устарел ли граф knowledge после правок в modules/.
# Не делает rebuild сам — только напоминает (rebuild часто тяжёлый, не место для авто-запуска).

set -e

# Читаем вход от Claude Code. Если этот Stop уже вызван самим stop-хуком
# (stop_hook_active=true), выходим тихо — иначе additionalContext заставит
# модель продолжить, она снова остановится, хук снова сработает → бесконечный цикл.
INPUT=$(cat 2>/dev/null || true)
if printf '%s' "$INPUT" | grep -q '"stop_hook_active"[[:space:]]*:[[:space:]]*true'; then
  exit 0
fi

# Если графа нет — выходим тихо.
[ -f graphify-out/graph.json ] || exit 0

# Список модифицированных source-файлов в modules/ относительно HEAD.
CHANGED=$(git diff --name-only HEAD -- 'modules/**/*.java' 'modules/**/*.kt' 'modules/**/*.sql' 'modules/**/*.yml' 2>/dev/null | head -5)

# Плюс untracked файлы.
UNTRACKED=$(git ls-files --others --exclude-standard 'modules/**/*.java' 'modules/**/*.kt' 2>/dev/null | head -5)

if [ -z "$CHANGED" ] && [ -z "$UNTRACKED" ]; then
  exit 0
fi

FILES=$(printf '%s\n%s' "$CHANGED" "$UNTRACKED" | sed '/^$/d' | head -5 | tr '\n' ',' | sed 's/,$//')

CONTEXT="graphify: uncommitted changes in modules/ (${FILES}). Graph may be stale. To refresh: \$(cat graphify-out/.graphify_python) -c 'from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path(\".\"))'"

ESCAPED=$(printf '%s' "$CONTEXT" | python3 -c 'import json,sys;print(json.dumps(sys.stdin.read()))')

printf '{"hookSpecificOutput":{"hookEventName":"Stop","additionalContext":%s}}\n' "$ESCAPED"
