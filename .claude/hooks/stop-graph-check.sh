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

SID=$(printf '%s' "$INPUT" | python3 -c 'import json,sys
try: print(json.load(sys.stdin).get("session_id","") or "")
except Exception: print("")' 2>/dev/null || true)
[ -n "$SID" ] || SID="default"
BASELINE=".claude/hooks/state/graph-baseline-${SID}.txt"

# Все грязные modules/-source файлы сейчас.
CURRENT=$( {
  git diff --name-only HEAD -- 'modules/**/*.java' 'modules/**/*.kt' 'modules/**/*.sql' 'modules/**/*.yml' 2>/dev/null
  git ls-files --others --exclude-standard 'modules/**/*.java' 'modules/**/*.kt' 'modules/**/*.sql' 'modules/**/*.yml' 2>/dev/null
} | sed '/^$/d' | sort -u )

# Вычитаем baseline сессии — остаются только правки, сделанные В ЭТОЙ сессии.
# Файлы, что были грязными на старте (чужой pre-existing diff), не напоминаем.
if [ -f "$BASELINE" ]; then
  NEW=$(comm -23 <(printf '%s\n' "$CURRENT") "$BASELINE")
else
  NEW="$CURRENT"
fi

if [ -z "$NEW" ]; then
  exit 0
fi

FILES=$(printf '%s\n' "$NEW" | sed '/^$/d' | head -5 | tr '\n' ',' | sed 's/,$//')

CONTEXT="graphify: uncommitted changes in modules/ (${FILES}). Graph may be stale. To refresh: \$(cat graphify-out/.graphify_python) -c 'from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path(\".\"))'"

ESCAPED=$(printf '%s' "$CONTEXT" | python3 -c 'import json,sys;print(json.dumps(sys.stdin.read()))')

printf '{"hookSpecificOutput":{"hookEventName":"Stop","additionalContext":%s}}\n' "$ESCAPED"
