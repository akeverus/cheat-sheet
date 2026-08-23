#!/usr/bin/env python3
"""ARCH-02: вынос CSS-эссе (>1KB) в docs/css-decisions/<file>.md.

Инвариант: stripped-CSS (без комментариев, нормализованные пробелы) байт-в-байт
не меняется — правки только внутри комментариев.
"""
import re, sys, hashlib
from pathlib import Path

CSS = Path(sys.argv[1])
APPLY = len(sys.argv) > 2 and sys.argv[2] == 'apply'
NAME = CSS.name                       # tokens.css
STEM = NAME.replace('.css', '')       # tokens
MD = Path('docs/css-decisions') / (STEM + '.md')
LIMIT = 1024

src = CSS.read_text(encoding='utf-8')

def stripped(s):
    s2 = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    return re.sub(r'\s+', ' ', s2).strip()

before_hash = hashlib.sha256(stripped(src).encode()).hexdigest()

comments = [(m.start(), m.end(), m.group()) for m in re.finditer(r'/\*.*?\*/', src, re.S)]
essays = [(s, e, t) for s, e, t in comments if len(t.encode()) > LIMIT]

def thesis(text):
    body = text[2:-2].strip()
    body = re.sub(r'^[=\-\s*]+', '', body)
    # первый абзац -> первое предложение (по '. ' или концу строки-тезиса)
    first_par = re.split(r'\n\s*\n', body)[0]
    one = ' '.join(l.strip() for l in first_par.splitlines())
    one = re.sub(r'\s*-{3,}\s*', ' — ', one)
    one = re.sub(r'\s{2,}', ' ', one)
    m = re.match(r'(.+?[.!?])(\s|$)', one)
    t = m.group(1) if m else one
    return (t[:177] + '…') if len(t) > 180 else t

def context_line(pos_end):
    tail = src[pos_end:]
    for line in tail.splitlines():
        l = line.strip()
        if l and not l.startswith('/*'):
            return l[:120]
    return '(конец файла)'

md_parts = [f"""# {NAME} — досье решений (ARCH-02)

Полные доводы и история правил, вынесенные из комментариев `{NAME}`
(REF-009): эссе читают при ПЕРЕСМОТРЕ решения, а не при каждом чтении файла.
У правила в CSS остаётся указатель с тезисом; якоря ниже — цель указателей.
Рабочие инварианты («НЕ менять», пороги) остаются в CSS.
"""]
out = []
last = 0
for i, (s, e, t) in enumerate(essays, 1):
    line_no = src[:s].count('\n') + 1
    aid = f"{STEM}-e{i:02d}"
    th = thesis(t)
    ctx = context_line(e)
    indent = ''
    ls = src.rfind('\n', 0, s) + 1
    indent = src[ls:s] if src[ls:s].strip() == '' else ''
    pointer = f"{'':s}/* {th}\n{indent}   Довод и история: docs/css-decisions/{STEM}.md#{aid} */".replace(f"{'':s}", '')
    pointer = f"/* {th}\n{indent}   Довод и история: docs/css-decisions/{STEM}.md#{aid} */"
    out.append(src[last:s] + pointer)
    last = e
    body = t[2:-2].strip()
    md_parts.append(f"""
<a id="{aid}"></a>
## {aid} · {NAME}:{line_no}

**Контекст:** `{ctx}`

{body}
""")
out.append(src[last:])
new_src = ''.join(out)

after_hash = hashlib.sha256(stripped(new_src).encode()).hexdigest()
ok = before_hash == after_hash
print(f"file={NAME} essays={len(essays)} bytes_out={sum(len(t.encode()) for _,_,t in essays)} stripped_identical={ok}")
for i, (s, e, t) in enumerate(essays, 1):
    line_no = src[:s].count('\n') + 1
    print(f"  e{i:02d} L{line_no} {len(t.encode()):>6}b :: {thesis(t)[:100]}")
if not ok:
    sys.exit('STRIPPED MISMATCH — правка вышла за пределы комментариев')
if APPLY:
    MD.parent.mkdir(parents=True, exist_ok=True)
    MD.write_text(''.join(md_parts), encoding='utf-8')
    CSS.write_text(new_src, encoding='utf-8')
    print(f"APPLIED: {CSS} ({len(src.encode())//1024}KB -> {len(new_src.encode())//1024}KB), {MD}")
