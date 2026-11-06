from pathlib import Path
from collections import Counter
import hashlib
import json
import re
import subprocess
import sys

r = Path('/Users/sergeyvoronin/IdeaProjects/cheat-sheet')
w = Path(__file__).resolve().parent
root = r / 'modules/quiz-app/src/main/resources/seed/mcq'
changed = set(subprocess.check_output(['git', 'diff', '--name-only', 'HEAD', '--', str(root)], cwd=r, text=True).splitlines())
recent = set(subprocess.check_output(['git', 'log', '--since=2026-11-04T00:00:00Z', '--format=', '--name-only', '--', str(root)], cwd=r, text=True).splitlines())
current = {'testing/property-based-testing-interview.json', 'testing/mockito-interview.json', 'ai-ml/vector-databases-interview.json'}
stops = set('the a an is are was were to of in for with without and or not be been can could will would should must does do this that these those it its from by as when then because which where using returns return has have'.split())
code_start = re.compile(r'^\s*(?:import\s|from\s+\S+\s+import\s|package\s|public\s|private\s|protected\s|class\s|def\s|assert\s|return\s|SELECT\s|FROM\s|WHERE\s|CREATE\s|INSERT\s|UPDATE\s|DELETE\s|@\w|//|/\*|\*|#|\{|\}|<\w)', re.I)


def prose(text):
    text = re.sub(r'```[\s\S]*?```', ' ', text)
    text = re.sub(r'`[^`]*`', ' ', text)
    text = re.sub(r'\[\[[^]]*\]\]', ' ', text)
    text = re.sub(r'https?://\S+', ' ', text)
    lines = []
    for line in text.splitlines():
        if code_start.search(line):
            continue
        if (';' in line or '=>' in line or ' = ' in line) and re.search(r'[{}()\[\]]', line):
            continue
        lines.append(line)
    return '\n'.join(lines)


counts = Counter()
sets = {name: Counter() for name in ['all', 'working_changed', 'recently_committed', 'current_three']}
candidates = []
latin_only = []
snapshots = {}
for p in sorted(root.rglob('*.json')):
    raw = p.read_bytes()
    d = json.loads(raw)
    rel = str(p.relative_to(r))
    topic = str(p.relative_to(root))
    snapshots[topic] = hashlib.sha256(raw).hexdigest()
    memberships = ['all'] + (['working_changed'] if rel in changed else []) + (['recently_committed'] if rel in recent else []) + (['current_three'] if topic in current else [])
    for group in memberships:
        sets[group]['files'] += 1
    for q in d['questions']:
        for b in q['blocks']:
            values = [('question_text', None, b.get('question_text', ''))]
            for o in b['options']:
                values.append(('option.text', o.get('label'), o['text']))
                values.extend(('sections.' + key, o.get('label'), value) for key, value in o['sections'].items() if isinstance(value, str))
            for field, label, value in values:
                kind = 'question_text' if field == 'question_text' else 'option.text' if field == 'option.text' else 'sections'
                clean = prose(value)
                words = re.findall(r'[A-Za-z]+', clean)
                cyr = re.findall(r'[А-Яа-яЁё]', clean)
                stop_count = sum(word.lower() in stops for word in words)
                for group in memberships:
                    sets[group][kind] += 1
                    sets[group]['fields_total'] += 1
                    if cyr:
                        sets[group]['contains_cyrillic'] += 1
                if not cyr and words:
                    item = dict(topic=topic, q=q['q_number'], b=b['block_idx'], label=label, field=field,
                                latin_words=len(words), stop_words=stop_count, memberships=memberships, text=value, clean=clean)
                    latin_only.append(item)
                strong = not cyr and len(words) >= 7 and stop_count >= 3
                mixed_sentences = []
                for sentence in re.split(r'(?<=[.!?])\s+|\n', clean):
                    en = re.findall(r'[A-Za-z]+', sentence)
                    ru = re.findall(r'[А-Яа-яЁё]', sentence)
                    if not ru and len(en) >= 8 and sum(x.lower() in stops for x in en) >= 3:
                        mixed_sentences.append(sentence)
                if strong or mixed_sentences:
                    item = dict(topic=topic, q=q['q_number'], b=b['block_idx'], label=label, field=field,
                                latin_words=len(words), stop_words=stop_count, memberships=memberships, text=value,
                                clean=clean, mixed_sentences=mixed_sentences)
                    candidates.append(item)
                    for group in memberships:
                        sets[group]['english_prose_candidates'] += 1

report = dict(method='Actual question_text/option.text/section strings, excluding metadata. Heuristic candidates require manual review; code, APIs and technical labels are not automatically English prose.',
              scope={k: dict(v) for k, v in sets.items()},
              candidate_count=len(candidates), latin_only_field_count=len(latin_only),
              candidates=candidates, latin_only=latin_only, snapshots=snapshots)
target = Path(sys.argv[1]) if len(sys.argv) > 1 else w / 'mcq-russian-content-audit.json'
target.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
print(json.dumps(dict(scope=report['scope'], candidate_count=len(candidates), latin_only_field_count=len(latin_only)), ensure_ascii=False, indent=2))
print('CANDIDATES')
for item in candidates[:35]:
    print(json.dumps({k: v for k, v in item.items() if k not in ['clean', 'memberships', 'mixed_sentences']} , ensure_ascii=False)[:1200])
