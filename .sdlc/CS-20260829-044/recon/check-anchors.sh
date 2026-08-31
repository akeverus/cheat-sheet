#!/usr/bin/env bash
# CS-20260829-044 — AC7/AC8: резолвит все path:line/path:A-B якоря основного
# документа и проверяет, что каждый буллет верхнего уровня в пяти рубриках
# (ПЛОХО/СЛУЧАЙНО/УСТАРЕЛО/ПЕРЕУСЛОЖНЕНО/НЕ ХВАТАЕТ) несёт хотя бы один якорь.
# Запуск из корня worktree: bash .sdlc/CS-20260829-044/recon/check-anchors.sh [--bullets]
set -euo pipefail
cd "$(dirname "$0")/../../.."

DOC="docs/redesign/01-forensic-audit.md"
MODE="${1:-}"

if [[ "$MODE" == "--bullets" ]]; then
  python3 - "$DOC" <<'PY'
import re, sys

doc_path = sys.argv[1]
with open(doc_path, encoding="utf-8") as f:
    lines = f.readlines()

RUBRICS = ("ЧТО ПЛОХО", "ЧТО СЛУЧАЙНО", "ЧТО УСТАРЕЛО", "ЧТО ПЕРЕУСЛОЖНЕНО", "ЧЕГО НЕ ХВАТАЕТ")
H2_RE = re.compile(r'^##\s+(.+?)\s*$')
BULLET_RE = re.compile(r'^-\s+\S')
ANCHOR_RE = re.compile(
    r'[A-Za-z0-9_./-]+/[A-Za-z0-9_.-]+\.[A-Za-z]+:\d+(?:-\d+)?'  # path:line или path:A-B
    r'|[A-Za-z][A-Za-z0-9]*ContractTest'                          # имя контракт-теста
    r'|[A-Za-z0-9_-]+\.spec\.js'                                  # e2e спек
    r'|[A-Za-z0-9_-]+\.png'                                       # имя PNG из shots/
)

current_rubric = None
in_target = False
unproven = []
bullet_start = None
bullet_text = []

def flush():
    global bullet_start, bullet_text
    if bullet_start is not None:
        text = ''.join(bullet_text)
        if not ANCHOR_RE.search(text):
            unproven.append((bullet_start, text.splitlines()[0][:100]))
    bullet_start = None
    bullet_text = []

for i, line in enumerate(lines, start=1):
    h2 = H2_RE.match(line)
    if h2:
        flush()
        in_target = h2.group(1).strip() in RUBRICS
        current_rubric = h2.group(1).strip()
        continue
    if not in_target:
        continue
    if BULLET_RE.match(line):
        flush()
        bullet_start = i
        bullet_text = [line]
    elif bullet_start is not None:
        # продолжение буллета: строка с отступом, не новый top-level буллет/заголовок
        if line.strip() == '' or line.startswith('  ') or line.startswith('\t'):
            bullet_text.append(line)
        else:
            flush()

flush()

if unproven:
    for ln, text in unproven:
        print(f"UNPROVEN {doc_path}:{ln}: {text}")
print(f"{len(unproven)} unproven")
sys.exit(0 if not unproven else 1)
PY
  exit $?
fi

python3 - "$DOC" <<'PY'
import re, os, sys

doc_path = sys.argv[1]
with open(doc_path, encoding="utf-8") as f:
    text = f.read()

ANCHOR_RE = re.compile(r'([A-Za-z0-9_./-]+/[A-Za-z0-9_.-]+\.[A-Za-z]+):(\d+)(?:-(\d+))?')

broken = []
seen = set()
for m in ANCHOR_RE.finditer(text):
    path, a, b = m.group(1), int(m.group(2)), m.group(3)
    key = (path, a, b)
    if key in seen:
        continue
    seen.add(key)
    if not os.path.isfile(path):
        broken.append(f"{path}:{a}{'-'+b if b else ''} — файл не найден")
        continue
    with open(path, encoding="utf-8", errors="replace") as f:
        total = sum(1 for _ in f)
    if a > total:
        broken.append(f"{path}:{a}{'-'+b if b else ''} — начало {a} > {total} строк в файле")
        continue
    if b is not None and int(b) > total:
        broken.append(f"{path}:{a}-{b} — конец {b} > {total} строк в файле")

if broken:
    for line in broken:
        print(f"BROKEN {line}")
print(f"{len(broken)} broken")
sys.exit(0 if not broken else 1)
PY
