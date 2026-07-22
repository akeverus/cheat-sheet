#!/usr/bin/env bash
# Run / update visual goldens (Playwright). App must be up, or pass --start-server.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
E2E="$ROOT/e2e"
UPDATE=0
START_SERVER=0

usage() {
  cat <<'EOF'
Usage: e2e/scripts/run-visual-goldens.sh [--update] [--start-server]

  --update         rewrite e2e/visual-baselines (playwright --update-snapshots)
  --start-server   set E2E_START_SERVER=1 so Playwright boots Gradle bootRun

Env:
  E2E_BASE_URL     default http://localhost:8080
  VISUAL_GOLDEN=1  always set by this script
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --update) UPDATE=1; shift ;;
    --start-server) START_SERVER=1; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown arg: $1" >&2; usage; exit 2 ;;
  esac
done

cd "$E2E"
if [[ ! -d node_modules/@playwright/test ]]; then
  npm ci
  npx playwright install chromium
fi

export VISUAL_GOLDEN=1
export E2E_BASE_URL="${E2E_BASE_URL:-http://localhost:8080}"
if [[ "$START_SERVER" -eq 1 ]]; then
  export E2E_START_SERVER=1
fi

ARGS=(test visual-matrix.spec.js)
if [[ "$UPDATE" -eq 1 ]]; then
  ARGS+=(--update-snapshots)
fi

echo "VISUAL_GOLDEN=1 E2E_BASE_URL=$E2E_BASE_URL E2E_START_SERVER=${E2E_START_SERVER:-0}"
npx playwright "${ARGS[@]}"
echo "Baselines: $E2E/visual-baselines"
