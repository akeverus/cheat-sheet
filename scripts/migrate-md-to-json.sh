#!/usr/bin/env bash
# migrate-md-to-json.sh — конвертирует один .md (v2 inline MCQ) в .json seed + чистит .md.
# Usage: bash scripts/migrate-md-to-json.sh cheatsheets/interview/<category>/<topic>.md

set -euo pipefail

MD_FILE="$1"
[[ -f "$MD_FILE" ]] || { echo "file not found: $MD_FILE"; exit 1; }

# Compute paths
REL=${MD_FILE#cheatsheets/interview/}
CATEGORY=$(dirname "$REL")
BASENAME=$(basename "$REL" .md)
JSON_DIR="modules/quiz-app/src/main/resources/seed/mcq/$CATEGORY"
JSON_FILE="$JSON_DIR/$BASENAME.json"

mkdir -p "$JSON_DIR"

# Detect awk variant
if ! command -v gawk >/dev/null 2>&1; then
    echo "ERROR: gawk is required (3-arg match() syntax). Install: brew install gawk" >&2
    exit 2
fi
AWK=gawk

# Run awk extractor → TSV
TSV=$("$AWK" -f scripts/extract-mcq-blocks.awk "$MD_FILE")
if [[ -z "$TSV" ]]; then
    echo "no MCQ blocks found in $MD_FILE"
    exit 0
fi

# Build JSON via python3 (one-shot migration tool, not generation)
JSON_BUILD=$(printf '%s\n' "$TSV" | python3 -c "
import sys, json, collections
sec_map = {
    'Развёрнутое объяснение': 'explanation',
    'Пример': 'example',
    'Когда применять': 'when_to_apply',
    'Подводные камни': 'edge_cases',
    'Связанные вопросы': 'related',
    'Что на самом деле': 'what_actually',
    'Откуда путаница': 'source_of_confusion',
    'Откуда путаника': 'source_of_confusion',   # typo variant in source
    'Если бы это было правдой': 'if_it_were_true',
    'Как было бы правильно': 'how_it_should_be',
    'Как было bы правильно': 'how_it_should_be',  # mixed-script typo
    'Как было bly правильно': 'how_it_should_be',  # typo variant
    'Как было bzy правильно': 'how_it_should_be',  # typo variant
    'Как было bı правильно': 'how_it_should_be',   # typo variant
    'Как было bылo правильно': 'how_it_should_be', # typo variant
}
data = collections.defaultdict(lambda: collections.defaultdict(lambda: collections.defaultdict(lambda: {'sections': {}})))
qtitles = {}  # q_number -> title text
for line in sys.stdin:
    parts = line.rstrip('\n').split('\t')
    if len(parts) != 7:
        continue
    q, blk, label, correct, text, sec, content = parts
    if text == 'QTITLE':
        qtitles[int(q)] = content
        continue
    key = sec_map.get(sec)
    if not key:
        # Fallback: prefix match (e.g. 'Когда применять защиту' → 'Когда применять')
        for prefix, mapped in sec_map.items():
            if sec.startswith(prefix):
                key = mapped
                break
    if not key:
        continue
    opt = data[int(q)][int(blk)][label]
    opt['label'] = label
    opt['text'] = text
    opt['correct'] = (correct == 'true')
    opt['sections'][key] = content
out = {'topic_slug': '$BASENAME', 'questions': []}
import sys as _sys
for qn in sorted(data.keys()):
    blocks = []
    qtitle = qtitles.get(qn) or 'Question ' + str(qn)
    if not qtitle.strip():
        qtitle = '<no question text>'
    for bi in sorted(data[qn].keys()):
        options = []
        labels = sorted(data[qn][bi].keys())
        # Schema requires exactly 4 options labeled A,B,C,D.
        if labels != ['A', 'B', 'C', 'D']:
            print(f'  skip: Q{qn} block {bi} has labels {labels} (not A,B,C,D) — likely legacy/non-v2 MCQ', file=_sys.stderr)
            continue
        for order, lbl in enumerate(labels):
            o = data[qn][bi][lbl]
            o['order'] = order
            options.append(o)
        blocks.append({'block_idx': bi, 'question_text': qtitle, 'options': options})
    if blocks:
        # Re-index blocks 0..N within each question after possible skips
        for new_idx, b in enumerate(blocks):
            b['block_idx'] = new_idx
        out['questions'].append({'q_number': qn, 'blocks': blocks})
print(json.dumps(out, ensure_ascii=False, indent=2))
")

echo "$JSON_BUILD" > "$JSON_FILE"

# Strip MCQ blocks from .md
"$AWK" '
    /^> \[!mcq\]/ { in_mcq = 1; next }
    in_mcq && /^>/ { next }
    in_mcq && /^[[:space:]]*$/ { in_mcq = 0; next }
    { print }
' "$MD_FILE" > "$MD_FILE.tmp" && mv "$MD_FILE.tmp" "$MD_FILE"

# Remove mcq_format_version from frontmatter (BSD sed compat: use -i '')
case "$(uname)" in
    Darwin) sed -i '' '/^mcq_format_version:/d' "$MD_FILE" ;;
    *)      sed -i '/^mcq_format_version:/d' "$MD_FILE" ;;
esac

echo "Migrated: $MD_FILE → $JSON_FILE"
