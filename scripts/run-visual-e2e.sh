#!/usr/bin/env bash
# TEST-005: Playwright visual matrix E2E против локального bootRun.
# Требует: Node/npm, Docker (postgres), JDK/Gradle.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

BASE_URL="${E2E_BASE_URL:-http://localhost:8080}"

echo "==> postgres"
docker compose up -d postgres

echo "==> e2e deps"
(cd e2e && npm install --silent && npx playwright install chromium)

if ! curl -sf -o /dev/null "$BASE_URL/"; then
  echo "==> starting bootRun in background"
  ./gradlew :quiz-app:bootRun -q > /tmp/cheat-sheet-bootrun.log 2>&1 &
  BOOT_PID=$!
  trap 'kill $BOOT_PID 2>/dev/null || true' EXIT
  for i in $(seq 1 90); do
    if curl -sf -o /dev/null "$BASE_URL/"; then
      echo "app ready (${i}s)"
      break
    fi
    if ! kill -0 "$BOOT_PID" 2>/dev/null; then
      echo "bootRun exited early; log:"
      tail -80 /tmp/cheat-sheet-bootrun.log || true
      exit 1
    fi
    sleep 2
  done
  curl -sf -o /dev/null "$BASE_URL/" || { echo "timeout waiting for $BASE_URL"; exit 1; }
fi

echo "==> playwright visual matrix"
(cd e2e && E2E_BASE_URL="$BASE_URL" npm run test:visual)
echo "TEST-005 visual OK — screenshots in e2e/visual-results/"
