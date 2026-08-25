#!/usr/bin/env bash
# lint-scripts.selftest.sh — self-test для scripts/lint-scripts.sh.
#
# Usage:
#   bash scripts/tests/lint-scripts.selftest.sh
#
# Самодостаточный раннер без внешних зависимостей (в репо нет bats/pytest вне
# modules/). Каждый кейс поднимает свои фикстуры в mktemp -d и убирает их
# через trap ... RETURN. Ни один намеренно битый .sh/.py не попадает под
# scripts/ в git — только во временные каталоги.

set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LINT="$REPO_ROOT/scripts/lint-scripts.sh"

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

lint_whenAllScriptsValid_thenExitsZeroAndPrintsNothing() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/a.sh" <<'EOF'
#!/usr/bin/env bash
echo "hi"
EOF
    cat > "$dir/b.py" <<'EOF'
print("hi")
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 0 "$rc" || return 1
    assert_empty "$out" || return 1
}

lint_whenShellSyntaxBroken_thenExitsOneAndNamesThatFile() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/broken.sh" <<'EOF'
#!/usr/bin/env bash
if [[ true
    echo "missing then"
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "broken.sh" || return 1
}

lint_whenPythonSyntaxBroken_thenExitsOneAndNamesThatFile() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/broken.py" <<'EOF'
def f(:
    pass
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "broken.py" || return 1
}

lint_whenSeveralFilesBroken_thenListsEveryBrokenFileNotJustFirst() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/broken1.sh" <<'EOF'
#!/usr/bin/env bash
if [[ true
EOF
    cat > "$dir/broken2.py" <<'EOF'
def f(:
    pass
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "broken1.sh" || return 1
    assert_contains "$out" "broken2.py" || return 1
}

lint_whenBrokenScriptLiesInNestedSubdir_thenExitsOne() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    mkdir -p "$dir/sub/deep"
    cat > "$dir/sub/deep/broken.sh" <<'EOF'
#!/usr/bin/env bash
if [[ true
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "sub/deep/broken.sh" || return 1
}

lint_whenTargetDirMissingOrHasNoScripts_thenExitsOneWithExplanation() {
    local out rc
    out=$("$LINT" "/nonexistent/path/for/lint-scripts-selftest" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "not found" || return 1

    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    echo "no scripts here" > "$dir/readme.txt"
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 1 "$rc" || return 1
    assert_contains "$out" "files found" || return 1
}

lint_whenInvokedFromUnrelatedCwd_thenStillLintsItsOwnRepoScripts() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    local out rc
    out=$(cd "$dir" && "$LINT" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 0 "$rc" || return 1
}

lint_whenPythonOnlyEmitsSyntaxWarning_thenExitsZeroAndStaysSilent() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/warns.py" <<'EOF'
pattern = "\s+"
EOF
    local out rc
    out=$("$LINT" "$dir" 2>&1 </dev/null)
    rc=$?
    assert_exit_code 0 "$rc" || return 1
    assert_empty "$out" || return 1
}

lint_whenPythonCompiled_thenLeavesNoPycacheAndNoWorktreeChanges() {
    local dir
    dir=$(mktemp -d)
    trap 'rm -rf "$dir"' RETURN
    cat > "$dir/ok.py" <<'EOF'
print("ok")
EOF
    "$LINT" "$dir" >/dev/null 2>&1 </dev/null
    if [[ -d "$dir/__pycache__" ]]; then
        fail "__pycache__ created next to fixture sources"
        return 1
    fi

    local before after
    before=$(git -C "$REPO_ROOT" status --porcelain)
    "$LINT" >/dev/null 2>&1 </dev/null
    after=$(git -C "$REPO_ROOT" status --porcelain)
    if [[ "$before" != "$after" ]]; then
        fail "git status changed after running lint-scripts against the real repo"
        return 1
    fi
}

lint_whenRunAgainstRealRepoScripts_thenExitsZero() {
    local rc
    "$LINT" >/dev/null 2>&1 </dev/null
    rc=$?
    assert_exit_code 0 "$rc" || return 1
}

TESTS=(
    lint_whenAllScriptsValid_thenExitsZeroAndPrintsNothing
    lint_whenShellSyntaxBroken_thenExitsOneAndNamesThatFile
    lint_whenPythonSyntaxBroken_thenExitsOneAndNamesThatFile
    lint_whenSeveralFilesBroken_thenListsEveryBrokenFileNotJustFirst
    lint_whenBrokenScriptLiesInNestedSubdir_thenExitsOne
    lint_whenTargetDirMissingOrHasNoScripts_thenExitsOneWithExplanation
    lint_whenInvokedFromUnrelatedCwd_thenStillLintsItsOwnRepoScripts
    lint_whenPythonOnlyEmitsSyntaxWarning_thenExitsZeroAndStaysSilent
    lint_whenPythonCompiled_thenLeavesNoPycacheAndNoWorktreeChanges
    lint_whenRunAgainstRealRepoScripts_thenExitsZero
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
    echo -e "${GRN}lint-scripts.selftest: $PASS/$((PASS + FAIL)) passed${NC}"
    exit 0
fi
echo -e "${RED}lint-scripts.selftest: $FAIL/$((PASS + FAIL)) failed${NC}"
exit 1
