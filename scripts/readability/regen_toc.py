#!/usr/bin/env python3
"""Регенерировать ТОЛЬКО Q-ссылки в блоке `## Содержание` из текущих заголовков.

В отличие от scripts/cheatsheet-regenerate-toc.py (пересобирает весь TOC, схлопывает
дефисы, пропускает interview/), этот скрипт:
  - трогает только строки `- [Qn. …](#…)` внутри блока `## Содержание`;
  - групповые под-заголовки (`**Индексы**`), `[Полезные ссылки]`, `[See also]`,
    обзорную прозу — НЕ трогает;
  - текст ссылки = текущий заголовок минус `## `, якорь = GitHub-точный slug (slug.py);
  - матч по номеру Qn (устойчив к переводу текста заголовка);
  - no-op для файлов без `## Содержание`; идемпотентен.

Usage: regen_toc.py <md> [<md> ...]    # правит на месте, печатает изменённые
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import Dedup, _heading_linktext  # noqa: E402

RE_TOC = re.compile(r'^##\s+Содержание\s*$')
RE_H2 = re.compile(r'^##\s+')
RE_HEADING = re.compile(r'^#{1,6}\s+')
RE_FENCE = re.compile(r'^\s*```')
RE_TOC_Q = re.compile(r'^(\s*[-*]\s+)\[(Q(\d+)\.[^\]]*)\]\(#[^)]*\)\s*$')


def build_q_anchors(lines):
    """{qnum: (linktext, anchor)} — реплеем Dedup по ВСЕМ заголовкам в порядке файла,
    чтобы суффиксы -1/-2 совпали с GitHub."""
    ded = Dedup()
    in_code = False
    in_fm = bool(lines and lines[0].strip() == '---')
    qmap = {}
    for i, l in enumerate(lines):
        if in_fm:
            if i > 0 and l.strip() == '---':
                in_fm = False
            continue
        if RE_FENCE.match(l):
            in_code = not in_code
            continue
        if in_code:
            continue
        if RE_HEADING.match(l):
            lt = _heading_linktext(l)
            anchor = ded.make(lt)
            qm = re.match(r'^Q(\d+)\.', lt)
            if qm:
                qmap[int(qm.group(1))] = (lt, anchor)
    return qmap


def process(path):
    text = open(path, encoding='utf-8').read()
    lines = text.split('\n')
    # границы TOC
    start = next((i for i, l in enumerate(lines) if RE_TOC.match(l)), None)
    if start is None:
        return False
    end = len(lines)
    for j in range(start + 1, len(lines)):
        if RE_H2.match(lines[j]):
            end = j
            break
    qmap = build_q_anchors(lines)
    changed = False
    for k in range(start + 1, end):
        m = RE_TOC_Q.match(lines[k])
        if not m:
            continue
        n = int(m.group(3))
        if n not in qmap:
            continue
        lt, anchor = qmap[n]
        newline = f'{m.group(1)}[{lt}](#{anchor})'
        if newline != lines[k]:
            lines[k] = newline
            changed = True
    if changed:
        open(path, 'w', encoding='utf-8').write('\n'.join(lines))
    return changed


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('usage: regen_toc.py <md> [<md> ...]', file=sys.stderr)
        sys.exit(2)
    for p in sys.argv[1:]:
        if process(p):
            print(f'regenerated TOC: {p}')
