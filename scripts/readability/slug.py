#!/usr/bin/env python3
"""GitHub-slugger-совместимый slugify для якорей заголовков (cyrillic-aware).

GitHub (github-slugger) НЕ схлопывает повторяющиеся дефисы: `Q1. (!) Какова`
даёт `q1--какова` (двойной дефис из `. (!) `). Существующий
scripts/cheatsheet-regenerate-toc.py схлопывает `-+`→`-` — для interview-файлов
это НЕВЕРНО, поэтому здесь отдельная реализация.

Алгоритм:
  1. strip + lower
  2. снять backticks (keep content)
  3. удалить всё, кроме \\w (incl. кириллица под re.U), пробелов, дефиса
  4. каждый whitespace-символ → '-' (БЕЗ схлопывания)
  5. дедуп в пределах файла: повтор → '-1', '-2', ...

Self-test: для всех interview .md с TOC slug(текст-заголовка-после '## ')
должен байт-в-байт совпасть с якорем в соответствующей TOC-строке.
Usage:
  slug.py "Q5. Что такое X?"      # печатает slug
  slug.py --selftest              # сверка с 293 существующими TOC
"""
import os
import re
import sys

MDROOT = 'cheatsheets/interview'
_BACKTICK = re.compile(r'`([^`]+)`')
_NONSLUG = re.compile(r'[^\w\s-]', re.UNICODE)
_WS = re.compile(r'\s')


def slug(text):
    s = text.strip().lower()
    s = _BACKTICK.sub(r'\1', s)
    s = _NONSLUG.sub('', s)
    s = _WS.sub('-', s)
    return s


class Dedup:
    """Повтор slug в пределах одного файла получает суффикс -1/-2 (как GitHub)."""
    def __init__(self):
        self.seen = {}

    def make(self, text):
        base = slug(text)
        if base in self.seen:
            self.seen[base] += 1
            return f'{base}-{self.seen[base]}'
        self.seen[base] = 0
        return base


def _heading_linktext(line):
    """Из '## Q5. текст' → 'Q5. текст' (то, что становится текстом TOC-ссылки)."""
    return re.sub(r'^#{1,6}\s+', '', line.rstrip())


def _norm(a):
    # collapse повторяющихся дефисов и обрезка краёв — для отделения «конвенции дефисов»
    # (старый collapsing-генератор) от настоящего расхождения символов/букв.
    return re.sub(r'-+', '-', a).strip('-')


def selftest():
    realbug = []   # toc-текст == заголовку И после нормализации дефисов anchor != slug → НАСТОЯЩАЯ ошибка
    hyphen = 0     # отличие только в кратности дефисов (мой slug = GitHub-точный, committed = collapsed)
    stale = 0      # toc-текст отличается от заголовка → устаревший TOC (не наша вина)
    files = 0
    checked = 0
    for dp, _, fns in os.walk(MDROOT):
        for fn in fns:
            if not fn.endswith('-interview.md'):
                continue
            path = os.path.join(dp, fn)
            lines = open(path, encoding='utf-8').read().splitlines()
            # TOC по Q-номеру: { n: (linktext, anchor) }
            toc = {}
            for l in lines:
                m = re.match(r'^\s*[-*]\s*\[(Q(\d+)\.[^\]]*)\]\(#([^)]*)\)', l)
                if m:
                    toc[int(m.group(2))] = (m.group(1), m.group(3))
            if not toc:
                continue
            files += 1
            ded = Dedup()
            for l in lines:
                if not re.match(r'^#{1,6}\s+', l):
                    continue
                lt = _heading_linktext(l)
                anchor = ded.make(lt)
                qm = re.match(r'^Q(\d+)\.', lt)
                if qm:
                    n = int(qm.group(1))
                    if n in toc:
                        checked += 1
                        toc_text, toc_anchor = toc[n]
                        if anchor != toc_anchor:
                            if toc_text.strip() != lt.strip():
                                stale += 1
                            elif _norm(anchor) == _norm(toc_anchor):
                                hyphen += 1
                            else:
                                realbug.append((path, n, toc_anchor, anchor))
    print(f'files-with-TOC={files} headings-checked={checked} '
          f'real-slug-bugs={len(realbug)} hyphen-convention-diffs={hyphen} '
          f'stale-TOC-entries={stale}')
    for path, n, exp, got in realbug[:60]:
        rel = path[len(MDROOT) + 1:]
        print(f'  REAL-BUG {rel} Q{n}: toc=#{exp}  slug=#{got}')
    return 0 if not realbug else 1


# Ground-truth якоря, взятые из реально работающего TOC postgresql-interview.md
# (committed, рендерится на GitHub). Регрессионный гейт для slug() — НЕ зависит от
# грязного корпуса (в репозитории ~430 старых неверных якорей от прежних тулов).
GOLDEN = [
    ('Q1. (!) Какова архитектура процессов PostgreSQL?',
     'q1--какова-архитектура-процессов-postgresql'),
    ('Q7. В чём разница между `JSON` и `JSONB`?',
     'q7-в-чём-разница-между-json-и-jsonb'),
    ('Q5. Что такое `pg_hba.conf` и для чего он используется?',
     'q5-что-такое-pg_hbaconf-и-для-чего-он-используется'),
    ('Q8. Как работают массивы (`arrays`) в PostgreSQL?',
     'q8-как-работают-массивы-arrays-в-postgresql'),
    ('Q10. Как работают пользовательские типы (`enum`, `composite`, `domain`)?',
     'q10-как-работают-пользовательские-типы-enum-composite-domain'),
    ('Q18. (!) Что такое покрывающий (`covering`) индекс и как его создать в PostgreSQL?',
     'q18--что-такое-покрывающий-covering-индекс-и-как-его-создать-в-postgresql'),
]


def golden():
    bad = []
    for text, expect in GOLDEN:
        got = slug(text)
        if got != expect:
            bad.append((text, expect, got))
    if bad:
        print('GOLDEN FAIL:')
        for text, expect, got in bad:
            print(f'  {text!r}\n    expect #{expect}\n    got    #{got}')
        return 1
    print(f'GOLDEN OK ({len(GOLDEN)} cases)')
    return 0


if __name__ == '__main__':
    if len(sys.argv) > 1 and sys.argv[1] == '--selftest':
        rc = golden()           # обязательный гейт
        selftest()              # диагностика корпуса (информационно)
        sys.exit(rc)
    elif len(sys.argv) > 1:
        print(slug(sys.argv[1]))
    else:
        print('usage: slug.py "<text>" | --selftest', file=sys.stderr)
        sys.exit(2)
