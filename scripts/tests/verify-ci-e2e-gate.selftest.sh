#!/usr/bin/env bash
# verify-ci-e2e-gate.selftest.sh — self-test для scripts/verify-ci-e2e-gate.sh.
#
# Usage:
#   bash scripts/tests/verify-ci-e2e-gate.selftest.sh
#
# Стиль — дословно scripts/tests/lint-scripts.selftest.sh: самодостаточный
# раннер без bats/pytest, helpers assert_exit_code/assert_contains/assert_empty,
# фикстуры — копии реального .github/workflows/ci.yml в mktemp -d с одной
# вырезанной/закомментированной строкой, уборка через trap ... RETURN. Ни
# одного намеренно битого workflow-файла в git — только во временных каталогах.

set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
GATE="$REPO_ROOT/scripts/verify-ci-e2e-gate.sh"
REAL_CI_YML="$REPO_ROOT/.github/workflows/ci.yml"

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' NC='\033[0m'
else
    RED='' GRN='' NC=''
fi

fail() {
    echo "assertion failed: $1"
}

assert_exit_code() {
    local expected="$1" actual="$2"
    if [[ "$actual" != "$expected" ]]; then
        fail "expected exit $expected, got $actual"
        return 1
    fi
}

assert_empty() {
    local actual="$1"
    if [[ -n "$actual" ]]; then
        fail "expected empty output, got: $actual"
        return 1
    fi
}

assert_contains() {
    local haystack="$1" needle="$2"
    if [[ "$haystack" != *"$needle"* ]]; then
        fail "expected output to contain '$needle', got: $haystack"
        return 1
    fi
}

# Удаляет строку 'e2e/**' только внутри блока триггера triggerKey (push или
# pull_request) блока on: — та же индент-логика, что и в самом гейте /
# CiE2eGateTest.extractTriggerPaths, чтобы не задеть второй триггер.
strip_e2e_glob_from_trigger() {
    local src="$1" trigger_key="$2"
    awk -v key="$trigger_key" '
        BEGIN { inside = 0 }
        {
            line = $0
            trimmed = line
            sub(/^[ \t]+/, "", trimmed)
            indent = length(line) - length(trimmed)
            if (indent == 2 && trimmed == key ":") {
                inside = 1
                print line
                next
            }
            if (inside && indent <= 2 && trimmed != "") {
                inside = 0
            }
            if (inside && trimmed == "- '"'"'e2e/**'"'"'") {
                next
            }
            print line
        }
    ' "$src"
}

gate_whenWorkflowHasE2eJobAndAllThreeSpecs_thenExitsZeroSilently() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cp "$REAL_CI_YML" "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 0 "$rc" || return 1
    assert_empty "$out" || return 1
}

gate_whenE2eJobBlockRemoved_thenExitsOneAndNamesTheJob() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    awk '/^  e2e:$/{skip=1} /^  docker-build:$/{skip=0} !skip' "$REAL_CI_YML" > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "e2e job: not declared" || return 1
}

gate_whenOneSpecDropped_thenExitsOneAndNamesThatSpec() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    grep -v 'reflow\.spec\.js' "$REAL_CI_YML" > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "spec not referenced: reflow.spec.js" || return 1
}

gate_whenPushPathsLackE2eGlob_thenExitsOneAndNamesPushTrigger() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    strip_e2e_glob_from_trigger "$REAL_CI_YML" push > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "push trigger" || return 1
}

gate_whenPullRequestPathsLackE2eGlob_thenExitsOneAndNamesPullRequestTrigger() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    strip_e2e_glob_from_trigger "$REAL_CI_YML" pull_request > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "pull_request trigger" || return 1
}

gate_whenPostgresServiceRemoved_thenExitsOne() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    grep -v 'image: postgres:16-alpine' "$REAL_CI_YML" > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "no postgres service" || return 1
}

gate_whenSeveralInvariantsBroken_thenReportsEveryOneNotJustFirst() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    awk '/^  e2e:$/{skip=1} /^  docker-build:$/{skip=0} !skip' "$REAL_CI_YML" > "$dir/tmp1.yml"
    strip_e2e_glob_from_trigger "$dir/tmp1.yml" push > "$dir/tmp2.yml"
    strip_e2e_glob_from_trigger "$dir/tmp2.yml" pull_request > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "e2e job: not declared" || return 1
    assert_contains "$out" "push trigger" || return 1
    assert_contains "$out" "pull_request trigger" || return 1
}

gate_whenWorkflowFileMissing_thenExitsOneWithExplanation() {
    local out rc
    out=$("$GATE" "/nonexistent/path/for/verify-ci-e2e-gate-selftest.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "not found" || return 1
}

gate_whenInvokedFromUnrelatedCwd_thenStillChecksItsOwnRepoWorkflow() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    local out rc
    out=$(cd "$dir" && "$GATE" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 0 "$rc" || return 1
    assert_empty "$out" || return 1
}

gate_whenSpecNamesAppearOnlyInCommentedOutLines_thenStillExitsOne() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    sed \
      -e "/^  e2e:\$/s/^/# /" \
      -e "/console-clean\.spec\.js/s/^/# /" \
      -e "/reflow\.spec\.js/s/^/# /" \
      -e "/keyboard-quiz\.spec\.js/s/^/# /" \
      -e "/'e2e\/\*\*'/s/^/# /" \
      -e "/image: postgres:16-alpine/s/^/# /" \
      "$REAL_CI_YML" > "$dir/ci.yml"
    local out rc
    out=$("$GATE" "$dir/ci.yml" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "e2e job: not declared" || return 1
    assert_contains "$out" "spec not referenced: console-clean.spec.js" || return 1
}

TESTS=(
    gate_whenWorkflowHasE2eJobAndAllThreeSpecs_thenExitsZeroSilently
    gate_whenE2eJobBlockRemoved_thenExitsOneAndNamesTheJob
    gate_whenOneSpecDropped_thenExitsOneAndNamesThatSpec
    gate_whenPushPathsLackE2eGlob_thenExitsOneAndNamesPushTrigger
    gate_whenPullRequestPathsLackE2eGlob_thenExitsOneAndNamesPullRequestTrigger
    gate_whenPostgresServiceRemoved_thenExitsOne
    gate_whenSeveralInvariantsBroken_thenReportsEveryOneNotJustFirst
    gate_whenWorkflowFileMissing_thenExitsOneWithExplanation
    gate_whenInvokedFromUnrelatedCwd_thenStillChecksItsOwnRepoWorkflow
    gate_whenSpecNamesAppearOnlyInCommentedOutLines_thenStillExitsOne
)

PASS=0
FAIL=0

for t in "${TESTS[@]}"; do
    out=$("$t" 2>&1)
    rc=$?
    if [[ "$rc" -eq 0 ]]; then
        PASS=$((PASS + 1))
        echo -e "${GRN}PASS${NC} $t"
    else
        FAIL=$((FAIL + 1))
        echo -e "${RED}FAIL${NC} $t"
        echo "$out" | sed 's/^/    /'
    fi
done

echo
if [[ "$FAIL" -eq 0 ]]; then
    echo -e "${GRN}verify-ci-e2e-gate.selftest: $PASS/$((PASS + FAIL)) passed${NC}"
    exit 0
fi
echo -e "${RED}verify-ci-e2e-gate.selftest: $FAIL/$((PASS + FAIL)) failed${NC}"
exit 1
