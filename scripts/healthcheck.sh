#!/usr/bin/env bash
# healthcheck.sh — локальный smoke-test поднятого приложения.
#
# Usage:
#   bash scripts/healthcheck.sh                  # дефолт http://localhost:8080
#   bash scripts/healthcheck.sh <base_url>       # кастомный host
#
# Возвращает 0 если все базовые endpoint'ы здоровы, 1 при первой проблеме.
# Используй после `./gradlew bootRun` или `docker compose up -d` для быстрой
# проверки что seed-импорт прошёл и readiness вышел в UP.

set -uo pipefail

BASE="${1:-http://localhost:8080}"

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' YLW='\033[0;33m' NC='\033[0m'
else
    RED='' GRN='' YLW='' NC=''
fi

ok()   { echo -e "  ${GRN}✓${NC} $1"; }
fail() { echo -e "  ${RED}✗${NC} $1"; FAILED=$((FAILED + 1)); }
warn() { echo -e "  ${YLW}!${NC} $1"; }

FAILED=0

check_status() {
    local label="$1"
    local url="$2"
    local expected="$3"
    local status
    status=$(curl -sS -o /dev/null -w "%{http_code}" --max-time 5 "$url" 2>/dev/null || echo "000")
    if [[ "$status" == "$expected" ]]; then
        ok "$label → HTTP $status"
    else
        fail "$label → HTTP $status (ожидался $expected)"
    fi
}

check_json_field() {
    local label="$1"
    local url="$2"
    local field="$3"
    local expected="$4"
    local actual
    actual=$(curl -sS --max-time 5 "$url" 2>/dev/null | python3 -c "
import json, sys
try:
    data = json.load(sys.stdin)
    keys = '$field'.split('.')
    for k in keys:
        data = data.get(k) if isinstance(data, dict) else None
    print(data if data is not None else '<missing>')
except Exception as e:
    print(f'<err: {e}>')
" 2>/dev/null)
    if [[ "$actual" == "$expected" ]]; then
        ok "$label.$field = $actual"
    else
        fail "$label.$field = $actual (ожидалось $expected)"
    fi
}

echo "Smoke test → $BASE"
echo ""

echo "1. Liveness (JVM alive)"
check_json_field "/actuator/health/liveness" "$BASE/actuator/health/liveness" "status" "UP"

echo "2. Readiness (DB + seedCoverage)"
check_json_field "/actuator/health/readiness" "$BASE/actuator/health/readiness" "status" "UP"

echo "3. Home page rendering"
check_status "GET /" "$BASE/" "200"

echo "4. Stats page rendering"
check_status "GET /stats" "$BASE/stats" "200"

echo "5. Settings page rendering"
check_status "GET /settings" "$BASE/settings" "200"

echo "6. Static asset (CSS)"
check_status "GET /css/styles.css" "$BASE/css/styles.css" "200"

echo ""
if [[ $FAILED -eq 0 ]]; then
    echo -e "${GRN}all checks passed${NC}"
    exit 0
else
    echo -e "${RED}$FAILED check(s) failed${NC}"
    exit 1
fi
