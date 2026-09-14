#!/usr/bin/env bash
# Приёмочные pre-checks одной темы. Семантическое и независимое ревью обязательны отдельно.
# Раньше grep/tail маскировали ошибки. Теперь сохраняем результат каждого gate.
set -uo pipefail
MCQ_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
if [[ $# -ne 1 ]]; then
    echo "Usage: gates.sh <seed.json>" >&2
    exit 2
fi
MCQ_INPUT="$(python3 -c 'import pathlib,sys; print(pathlib.Path(sys.argv[1]).resolve())' "$1")" || exit 2
bash "$MCQ_ROOT/scripts/verify-mcq-json.sh" "$MCQ_INPUT" || exit "$?"
MCQ_RESULT=0
run_gate() {
    "$@"
    local result=$?
    if [[ $result -gt 1 ]]; then
        MCQ_RESULT=2
    elif [[ $result -eq 1 && $MCQ_RESULT -eq 0 ]]; then
        MCQ_RESULT=1
    fi
}
run_gate python3 "$MCQ_ROOT/scripts/mcq-answer-parity-gate.py" "$MCQ_INPUT"
run_gate python3 "$MCQ_ROOT/scripts/mcq-anti-tell-scan.py" "$MCQ_INPUT"
run_gate python3 "$MCQ_ROOT/scripts/mcq-linkage-scan.py" "$MCQ_INPUT"
run_gate python3 "$MCQ_ROOT/scripts/mcq-skel-gate.py" "$MCQ_INPUT"
# Статистический отчёт — диагностика, а не самостоятельное доказательство качества.
python3 "$MCQ_ROOT/scripts/mcq-struct-scan.py" "$MCQ_INPUT" || MCQ_RESULT=2
python3 "$MCQ_ROOT/scripts/mcq-struct-report.py" "$MCQ_INPUT" || MCQ_RESULT=2
MCQ_MD="$(python3 -c 'import pathlib,sys; p=pathlib.Path(sys.argv[1]); r=pathlib.Path(sys.argv[2]); print(r/"cheatsheets/interview"/p.relative_to(r/"modules/quiz-app/src/main/resources/seed/mcq").with_suffix(".md"))' "$MCQ_INPUT" "$MCQ_ROOT")" || exit 2
python3 "$MCQ_ROOT/scripts/mcq-tools/verbatim.py" "$MCQ_INPUT" "$MCQ_MD" || MCQ_RESULT=2
printf 'MCQ pre-check exit=%s; semantic/independent acceptance is separate\n' "$MCQ_RESULT"
exit "$MCQ_RESULT"
