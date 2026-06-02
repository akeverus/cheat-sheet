#!/usr/bin/env python3
"""Authoritative-гейт LANE 6 для одного interview .md против git HEAD.

Самопроверка агента — лишь advisory. Коммитим файл ТОЛЬКО если verify.py PASS.
Проверки (рабочее дерево vs `git show HEAD:<md>`):
  1. множество и число `## Q` неизменны (выравнивание MCQ)
  2. конкатенация ```-блоков байт-идентична HEAD (код не тронут)
  3. frontmatter неизменен
  4. TOC-согласованность: Q-набор TOC == HEAD; для каждой TOC-Q-строки текст ==
     текущему заголовку, anchor == slug(заголовок) (внутренняя консистентность)
  5. 0 MCQ-callouts, 0 Tier-1 маркеров, баланс ```
  6. 0 полностью-английских заголовков (latin>6 и cyr==0 после снятия backticks/`(!)`)
  7. 0 generic англ. bold-меток из стоп-листа (proper-noun-метки разрешены)
  8. проза cyr: не ниже HEAD-0.02 И (>=0.55 или >=HEAD+0.02)
  9. gate_clean.py PASS (если передан json) — выравнивание JSON цело

Usage: verify.py <md> [<json>]    # exit 0 = PASS
"""
import os
import re
import subprocess
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import Dedup, _heading_linktext  # noqa: E402
from regen_toc import build_q_anchors, RE_TOC, RE_H2, RE_TOC_Q  # noqa: E402

GATE = '/tmp/gate_clean.py'
MARK = re.compile(r'❌ ПОСЛЕДСТВИЕ:|✓ ПРИМЕНЯТЬ:|📋 ПРАВИЛО:|🔗 См\. Q\d')

# generic англ. метки, которые ДОЛЖНЫ быть переведены (= ключи таблицы в промте).
# Имена продуктов/технологий (Kafka, Redis, API, CDN, …) сюда НЕ входят — разрешены.
STOP_LABELS = {
    'best practice', 'best practices', 'edge case', 'edge cases', 'use case',
    'use cases', 'rule of thumb', 'workflow', 'mitigation', 'mitigations',
    'pipeline', 'failover', 'latency', 'storage', 'recurrence', 'timeout',
    'production', 'compliance', 'idea', 'tradeoff', 'trade-off', 'example',
    'examples', 'gotcha', 'gotchas', 'pros', 'cons', 'when to use',
}
RE_LABEL = re.compile(r'^\s*[-*>]?\s*\*\*([^*]+?):\*\*')


def head_version(path):
    r = subprocess.run(['git', 'show', f'HEAD:{path}'], capture_output=True, text=True)
    return r.stdout if r.returncode == 0 else None


def qset(t):
    return sorted(int(m) for m in re.findall(r'(?m)^##\s*Q(\d+)\b', t))


def fenced(t):
    return '\n~~~\n'.join(re.findall(r'```.*?```', t, flags=re.S))


def frontmatter(t):
    m = re.match(r'^---.*?\n---', t, flags=re.S)
    return m.group(0) if m else ''


def toc_q_entries(t):
    """{n: (linktext, anchor)} из блока ## Содержание."""
    lines = t.split('\n')
    start = next((i for i, l in enumerate(lines) if RE_TOC.match(l)), None)
    if start is None:
        return {}
    end = len(lines)
    for j in range(start + 1, len(lines)):
        if RE_H2.match(lines[j]):
            end = j
            break
    out = {}
    for k in range(start + 1, end):
        m = re.match(r'^\s*[-*]\s+\[(Q(\d+)\.[^\]]*)\]\(#([^)]*)\)', lines[k])
        if m:
            out[int(m.group(2))] = (m.group(1), m.group(3))
    return out


def english_headings(t):
    bad = []
    for h in re.findall(r'(?m)^##\s*Q\d+\.\s*(.+)$', t):
        core = re.sub(r'`[^`]*`', '', h)
        core = re.sub(r'\(!\)', '', core)
        lat = len(re.findall(r'[a-zA-Z]', core))
        cyr = len(re.findall(r'[а-яА-ЯёЁ]', core))
        if lat > 6 and cyr == 0:
            bad.append(h.strip())
    return bad


def stop_labels(t):
    found = []
    in_code = False
    for l in t.split('\n'):
        if re.match(r'^\s*```', l):
            in_code = not in_code
            continue
        if in_code:
            continue
        m = RE_LABEL.match(l)
        if m and m.group(1).strip().lower() in STOP_LABELS:
            found.append(m.group(1).strip())
    return found


def body_cyr(t):
    t = re.sub(r'^---.*?\n---', '', t, count=1, flags=re.S)
    t = re.sub(r'```.*?```', '', t, flags=re.S)
    t = re.sub(r'`[^`]*`', '', t)
    t = re.sub(r'\[\[[^\]]*\]\]', '', t)
    t = re.sub(r'\]\([^)]*\)', '] ', t)
    t = re.sub(r'https?://\S+', '', t)
    t = re.sub(r'(?m)^#{1,6}\s.*$', '', t)
    lat = len(re.findall(r'[a-zA-Z]', t))
    cyr = len(re.findall(r'[а-яА-ЯёЁ]', t))
    return cyr / (lat + cyr) if (lat + cyr) else 1.0


def verify(md, js=None):
    cur = open(md, encoding='utf-8').read()
    old = head_version(md)
    problems = []
    if old is None:
        problems.append('no-HEAD')
        old = ''

    if qset(cur) != qset(old):
        problems.append(f'Q-set changed {qset(old)}->{qset(cur)}')
    if fenced(cur) != fenced(old):
        problems.append('code-blocks changed')
    if frontmatter(cur) != frontmatter(old):
        problems.append('frontmatter changed')

    # 4. TOC consistency
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

    eh = english_headings(cur)
    if eh:
        problems.append(f'{len(eh)} EN-heading(s): {eh[:3]}')
    sl = stop_labels(cur)
    if sl:
        problems.append(f'EN-labels: {sorted(set(sl))[:5]}')

    cur_c = body_cyr(cur)
    old_c = body_cyr(old) if old else cur_c
    if cur_c < old_c - 0.02:
        problems.append(f'cyr regressed {old_c:.2f}->{cur_c:.2f}')
    elif cur_c < 0.55 and cur_c < old_c + 0.02:
        problems.append(f'cyr={cur_c:.2f} (<0.55 и не улучшен)')

    if js and os.path.exists(js):
        g = subprocess.run(['python3', GATE, md, js], capture_output=True, text=True)
        if not (g.stdout.strip().splitlines() or [''])[-1].endswith('PASS'):
            problems.append('gate-FAIL')

    return cur_c, problems


def main():
    md = sys.argv[1]
    js = sys.argv[2] if len(sys.argv) > 2 else None
    cur_c, problems = verify(md, js)
    slug_name = md.split('/')[-1]
    if problems:
        print(f'BAD {slug_name} cyr={cur_c:.2f}  ' + '; '.join(problems))
        sys.exit(1)
    print(f'OK  {slug_name} cyr={cur_c:.2f}')
    sys.exit(0)


if __name__ == '__main__':
    main()
