#!/usr/bin/env bash
# SessionStart hook: вкидывает в системный контекст ключевые правила репо.
# Не пишет в stderr/stdout кроме JSON-ответа.

set -e

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
