#!/usr/bin/env bash
# SessionStart hook: вкидывает в системный контекст ключевые правила репо.
# Не пишет в stderr/stdout кроме JSON-ответа.

set -e

# Читаем вход от Claude Code (JSON c session_id и пр.).
INPUT=$(cat 2>/dev/null || true)
SID=$(printf '%s' "$INPUT" | python3 -c 'import json,sys
try: print(json.load(sys.stdin).get("session_id","") or "")
except Exception: print("")' 2>/dev/null || true)
[ -n "$SID" ] || SID="default"

# Снимок modules/-файлов, которые УЖЕ грязные на старте сессии.
# Stop-хук будет игнорировать их — напоминает только про правки этой сессии.
STATE_DIR=".claude/hooks/state"
mkdir -p "$STATE_DIR" 2>/dev/null || true
{
  git diff --name-only HEAD -- 'modules/**/*.java' 'modules/**/*.kt' 'modules/**/*.sql' 'modules/**/*.yml' 2>/dev/null
  git ls-files --others --exclude-standard 'modules/**/*.java' 'modules/**/*.kt' 'modules/**/*.sql' 'modules/**/*.yml' 2>/dev/null
} | sed '/^$/d' | sort -u > "${STATE_DIR}/graph-baseline-${SID}.txt" 2>/dev/null || true

if [ -f graphify-out/graph.json ] && [ -f graphify-out/GRAPH_REPORT.md ]; then
  GRAPH_STATE="ready"
else
  GRAPH_STATE="missing (run /graphify modules to build)"
fi

UNCOMMITTED=$(git status --porcelain 2>/dev/null | wc -l | tr -d ' ' || echo "0")

CONTEXT="cheat-sheet repo reminders:
(1) graphify graph: ${GRAPH_STATE} — read graphify-out/GRAPH_REPORT.md before any grep/glob (rule in CLAUDE.md).
(2) MCQ live ONLY in modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json. Never add MCQ-callouts or Tier 1 markers to .md cheatsheets (pre-commit will block).
(3) Language: cheatsheets and chat in Russian; file names in English.
(4) Working tree: ${UNCOMMITTED} uncommitted change(s).
(5) Custom subagents available: explorer / reviewer / verifier / refactorer / synthesizer (see .claude/agents/)."

# Экранируем кавычки и переводы строк для JSON.
ESCAPED=$(printf '%s' "$CONTEXT" | python3 -c 'import json,sys;print(json.dumps(sys.stdin.read()))')

printf '{"hookSpecificOutput":{"hookEventName":"SessionStart","additionalContext":%s}}\n' "$ESCAPED"
