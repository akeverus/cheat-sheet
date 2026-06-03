#!/usr/bin/env python3
"""gate_clean.py — MCQ-alignment gate для пары (interview .md, seed json).

Восстановлен после session-boundary (оригинал лежал в /tmp и был вычищен).
Проверяет инвариант выравнивания MCQ (см. feedback_mcq_alignment):
  1. JSON парсится и имеет {topic_slug, questions:[{q_number, blocks}, ...]}.
  2. topic_slug == имя файла без расширения (slug темы).
  3. q_number уникальны.
  4. КАЖДЫЙ q_number из JSON имеет соответствующий `## Q<N>` в .md
     (числа JSON ⊆ числа ## Q в .md — опции привязываются по номеру).

Печатает последней строкой `<basename> PASS` при успехе (verify.py проверяет
.endswith('PASS')), иначе `<basename> FAIL: <причины>` и exit 1.

Usage: gate_clean.py <md> <json>
"""
import json
import os
import re
import sys


def q_numbers_md(path):
    t = open(path, encoding='utf-8').read()
    # вне ```-блоков
    out, in_code = [], False
    for line in t.split('\n'):
        if re.match(r'^\s*```', line):
            in_code = not in_code
            continue
        if in_code:
            continue
        m = re.match(r'^##\s*Q(\d+)\b', line)
        if m:
            out.append(int(m.group(1)))
    return out


def main():
    if len(sys.argv) < 3:
        print('usage: gate_clean.py <md> <json>', file=sys.stderr)
        sys.exit(2)
    md, js = sys.argv[1], sys.argv[2]
    base = os.path.basename(md)
    problems = []

    try:
        d = json.load(open(js, encoding='utf-8'))
    except Exception as e:  # noqa: BLE001
        print(f'{base} FAIL: json-parse {e}')
        sys.exit(1)

    if not isinstance(d, dict) or 'questions' not in d:
        print(f'{base} FAIL: json-shape (no questions)')
        sys.exit(1)

    slug = d.get('topic_slug')
    expect_slug = os.path.basename(md)[:-3] if md.endswith('.md') else os.path.basename(md)
    if slug != expect_slug:
        problems.append(f'topic_slug {slug!r}!={expect_slug!r}')

    qnums = []
    for q in d['questions']:
        n = q.get('q_number')
        if n is None:
            problems.append('q without q_number')
        else:
            qnums.append(int(n))
    dups = sorted({n for n in qnums if qnums.count(n) > 1})
    if dups:
        problems.append(f'dup q_number {dups}')

    md_q = set(q_numbers_md(md))
    missing = sorted(n for n in set(qnums) if n not in md_q)
    if missing:
        problems.append(f'q_number без ## Q в md: {missing[:10]}')

    if problems:
        print(f'{base} FAIL: ' + '; '.join(problems))
        sys.exit(1)
    print(f'{base} PASS')
    sys.exit(0)


if __name__ == '__main__':
    main()
