#!/usr/bin/env python3
"""Гейт LANE 8A: удаление mermaid из одного interview/cheatsheet .md против git HEAD.

Стандартный verify.py тут НЕ годится — он требует байт-идентичности ВСЕХ
```-блоков, а LANE 8A их (mermaid) удаляет/конвертирует. Этот гейт разрешает
убрать ровно mermaid-фенсы и ничего больше из кода.

Проверки (рабочее дерево vs `git show HEAD:<md>`):
  1. множество и число `## Q` неизменны (выравнивание MCQ)
  2. 0 ```mermaid-фенсов осталось в рабочей версии
  3. конкатенация ВСЕХ НЕ-mermaid ```-блоков байт-идентична HEAD
     (тронули только mermaid; новых code-фенсов не добавили — конвертации
      должны быть прозой/списками, не ASCII-в-кодфенсе)
  4. frontmatter неизменен
  5. TOC-согласованность (заголовки не тронуты → TOC валиден)
  6. 0 MCQ-callouts, 0 Tier-1 маркеров, баланс ```
  7. 0 новых полностью-английских заголовков
  8. проза cyr не ниже HEAD-0.02 (конвертация mermaid→рус. проза не должна ронять)

Usage: verify_nomermaid.py <md> [<json>]   # exit 0 = PASS
"""
import os
import re
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from verify import (  # noqa: E402
    head_version, qset, frontmatter, body_cyr, english_headings,
    toc_q_entries, MARK, GATE,
)
from regen_toc import build_q_anchors  # noqa: E402


def _blocks(t):
    return re.findall(r'```.*?```', t, flags=re.S)


def _is_mermaid(b):
    first = b.split('\n', 1)[0]
    return first[3:].strip().lower().startswith('mermaid')


def mermaid_count(t):
    return sum(1 for b in _blocks(t) if _is_mermaid(b))


def nonmermaid_concat(t):
    return '\n~~~\n'.join(b for b in _blocks(t) if not _is_mermaid(b))


def verify(md, js=None):
    cur = open(md, encoding='utf-8').read()
    old = head_version(md)
    problems = []
    if old is None:
        problems.append('no-HEAD')
        old = ''

    if qset(cur) != qset(old):
        problems.append(f'Q-set changed {qset(old)}->{qset(cur)}')

    mc = mermaid_count(cur)
    if mc:
        problems.append(f'{mc} mermaid block(s) remain')

    if nonmermaid_concat(cur) != nonmermaid_concat(old):
        problems.append('non-mermaid code changed (тронут код помимо mermaid)')

    if frontmatter(cur) != frontmatter(old):
        problems.append('frontmatter changed')

    # TOC consistency
    cur_toc = toc_q_entries(cur)
    old_toc = toc_q_entries(old) if old else {}
    if set(cur_toc) != set(old_toc):
        problems.append(f'TOC Q-set changed {sorted(old_toc)}->{sorted(cur_toc)}')
    qmap = build_q_anchors(cur.split('\n'))
    toc_bad = []
    for n, (lt, anchor) in cur_toc.items():
        if n in qmap:
            exp_lt, exp_anchor = qmap[n]
            if lt.strip() != exp_lt.strip() or anchor != exp_anchor:
                toc_bad.append(n)
    if toc_bad:
        problems.append(f'TOC out-of-sync for Q{toc_bad} (run regen_toc.py)')

    if re.search(r'(?m)^>\s*\[!mcq', cur):
        problems.append('mcq-callout')
    if MARK.search(cur):
        problems.append('tier1-marker')
    if len(re.findall(r'(?m)^```', cur)) % 2:
        problems.append('unbalanced-fence')

    new_eh = set(english_headings(cur)) - set(english_headings(old))
    if new_eh:
        problems.append(f'new EN-heading(s): {sorted(new_eh)[:3]}')

    cur_c = body_cyr(cur)
    old_c = body_cyr(old) if old else cur_c
    if cur_c < old_c - 0.02:
        problems.append(f'cyr regressed {old_c:.2f}->{cur_c:.2f}')

    if js and os.path.exists(js):
        g = subprocess.run(['python3', GATE, md, js], capture_output=True, text=True)
        if not (g.stdout.strip().splitlines() or [''])[-1].endswith('PASS'):
            problems.append('gate-FAIL')

    return cur_c, problems


def main():
    md = sys.argv[1]
    js = sys.argv[2] if len(sys.argv) > 2 else None
    cur_c, problems = verify(md, js)
    name = md.split('/')[-1]
    if problems:
        print(f'BAD {name} cyr={cur_c:.2f}  ' + '; '.join(problems))
        sys.exit(1)
    print(f'OK  {name} cyr={cur_c:.2f}  (mermaid removed)')
    sys.exit(0)


if __name__ == '__main__':
    main()
