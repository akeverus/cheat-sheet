#!/usr/bin/env bash
# CS-20260828-003 Блок D — инвентарь цветовых литералов вне tokens.css (§18.4).
set -euo pipefail
cd "$(dirname "$0")/../../.."
python3 - <<'PY'
import re, glob, os, csv, json

CSS_DIR = "modules/quiz-app/src/main/resources/static/css"
OUT_TSV = ".sdlc/CS-20260828-003/data/hex-inventory.tsv"
TOKENS_FILE = os.path.join(CSS_DIR, "tokens.css")

files = sorted(glob.glob(os.path.join(CSS_DIR, "*.css")))

HEX_RE = re.compile(r'#[0-9a-fA-F]{3,8}\b')
RGB_RE = re.compile(r'\brgba?\([^)]*\)')
HSL_RE = re.compile(r'\bhsla?\([^)]*\)')
COMMENT_RE = re.compile(r'/\*.*?\*/', re.DOTALL)
NONPALETTE_HINTS = ('gradient', 'shadow', 'mask', 'data:image', 'transparent',
                     'filter', 'drop-shadow', 'url(', 'currentColor', 'outline')

def norm_hex(s):
    return s.lower()

def norm_generic(s):
    return re.sub(r'\s+', ' ', s.strip().lower())

# --- 1. Значения токенов (hex/rgb/hsl) из tokens.css ---
with open(TOKENS_FILE, encoding='utf-8') as f:
    tokens_text = f.read()
token_values = {
    'hex': set(norm_hex(m) for m in HEX_RE.findall(tokens_text)),
    'rgb': set(norm_generic(m) for m in RGB_RE.findall(tokens_text)),
    'hsl': set(norm_generic(m) for m in HSL_RE.findall(tokens_text)),
}

# --- 2. Собрать все occurrences по файлам, с разбивкой comment/code ---
# literal -> {'kind':..., 'files': {file: count}, 'comment_files': {file: count}}
records = {}

def record(literal, kind, file_rel, in_comment):
    key = (kind, literal)
    r = records.setdefault(key, {'kind': kind, 'literal': literal, 'files': {}, 'comment_only_files': {}})
    bucket = r['comment_only_files'] if in_comment else r['files']
    bucket[file_rel] = bucket.get(file_rel, 0) + 1

for path in files:
    rel = os.path.relpath(path, CSS_DIR)
    with open(path, encoding='utf-8') as f:
        text = f.read()
    # Позиции комментариев для определения "только в комментарии"
    comment_spans = [(m.start(), m.end()) for m in COMMENT_RE.finditer(text)]
    def in_any_comment(pos):
        return any(s <= pos < e for s, e in comment_spans)

    for regex, kind, norm in ((HEX_RE, 'hex', norm_hex), (RGB_RE, 'rgb', norm_generic), (HSL_RE, 'hsl', norm_generic)):
        for m in regex.finditer(text):
            lit = norm(m.group(0))
            record(lit, kind, rel, in_any_comment(m.start()))

# --- 3. Классификация ---
rows = []
for (kind, literal), r in records.items():
    files_code = r['files']
    files_comment = r['comment_only_files']
    all_files = sorted(set(files_code) | set(files_comment))
    total_occ = sum(files_code.values()) + sum(files_comment.values())
    only_in_comments = (len(files_code) == 0 and len(files_comment) > 0)
    is_tokens_file_only = all_files == ['tokens.css']
    outside_tokens_code_files = [fn for fn in files_code if fn != 'tokens.css']

    cls = None
    note = ''
    if only_in_comments:
        cls = 'C'
        note = 'встречается только внутри /* ... */ комментариев'
    elif literal in token_values[kind]:
        if outside_tokens_code_files:
            cls = 'T'
            note = 'точный дубль значения токена tokens.css, захардкожен вне tokens.css'
        else:
            cls = 'S'
            note = 'значение живёт только внутри tokens.css (легитимный источник)'
    else:
        # non-hex (rgb/hsl) или hex без совпадения с токеном
        sample_lines = []
        for fn in outside_tokens_code_files or list(files_code.keys()):
            with open(os.path.join(CSS_DIR, fn), encoding='utf-8') as f:
                ftext = f.read()
            idx = ftext.lower().find(literal if kind != 'hex' else literal)
            if idx == -1:
                continue
            line_start = ftext.rfind('\n', 0, idx) + 1
            line_end = ftext.find('\n', idx)
            sample_lines.append(ftext[line_start:line_end if line_end != -1 else None].lower())
            break
        context = ' '.join(sample_lines)
        if any(h in context for h in NONPALETTE_HINTS) or literal in ('transparent', 'currentcolor'):
            cls = 'N'
            note = 'не палитровое употребление (градиент/тень/маска/data-uri/transparent)'
        else:
            cls = 'P'
            note = 'палитровый цвет без токена'

    rows.append({
        'literal': literal,
        'kind': kind,
        'occurrences': total_occ,
        'files': ','.join(all_files),
        'class': cls,
        'note': note,
    })

rows.sort(key=lambda r: (-r['occurrences'], r['literal']))

os.makedirs(os.path.dirname(OUT_TSV), exist_ok=True)
with open(OUT_TSV, 'w', encoding='utf-8', newline='') as f:
    w = csv.writer(f, delimiter='\t')
    w.writerow(['literal', 'kind', 'occurrences', 'files', 'class', 'note'])
    for r in rows:
        w.writerow([r['literal'], r['kind'], r['occurrences'], r['files'], r['class'], r['note']])

# --- Сводка в stdout ---
from collections import Counter
cnt = Counter(r['class'] for r in rows)
print(f"Всего уникальных литералов: {len(rows)}")
print(f"По классам: {dict(cnt)}")
print(f"Класс T (дубли токенов): {[r['literal'] for r in rows if r['class']=='T']}")
print(f"Класс P (без токена), первые 20: {[r['literal'] for r in rows if r['class']=='P'][:20]}")
PY
