#!/bin/bash
# Все гейты одним вызовом, включая ВИЗУАЛЬНЫЙ анти-телл. Usage: gates.sh <seed.json>
SEED="$1"; R="$(cd "$(dirname "$0")/../.." && pwd)"
echo "=== PARITY ==="; python3 "$R/scripts/mcq-answer-parity-gate.py" "$SEED" 2>&1 | grep -E '✅|❌|FAIL\]'
echo "=== ANTI-TELL ==="; python3 "$R/scripts/mcq-anti-tell-scan.py" "$SEED" 2>&1 | grep -E 'TELL|ИТОГ'
echo "=== LINKAGE ==="; python3 "$R/scripts/mcq-linkage-scan.py" "$SEED" 2>&1 | grep -E 'WARN|ERROR|ИТОГ|OK —'
echo "=== SCHEMA ==="; bash "$R/scripts/verify-mcq-json.sh" "$SEED" 2>&1 | tail -1
python3 - "$SEED" <<'PY'
import json, re, sys
blob = open(sys.argv[1], encoding="utf-8").read()
print("CJK:", len(re.findall(r"[一-鿿぀-ヿ]", blob)),
      "| FORBID:", len(re.findall(r"якобы|будто|почему-то", blob)),
      "| html-ent:", len(re.findall(r"&(?:lt|gt|amp|quot);", blob)))
PY
echo "=== STRUCT (визуальный анти-телл, ОБЯЗАТЕЛЬНЫЙ) ==="
python3 "$R/scripts/mcq-struct-scan.py" "$SEED" 2>&1 | tail -6
python3 "$R/scripts/mcq-struct-report.py" "$SEED" 2>&1 | tail -1
echo "=== VERBATIM (дословность correct, ОБЯЗАТЕЛЬНЫЙ) ==="
MD="$(echo "$SEED" | sed 's#.*/seed/mcq/#'"$R"'/cheatsheets/interview/#; s#\.json$#.md#')"
if [ -f "$MD" ]; then
  python3 "$R/scripts/mcq-tools/verbatim.py" "$SEED" "$MD" 2>&1 | tail -6
else
  echo "источник не найден: $MD — замер пропущен"
fi
