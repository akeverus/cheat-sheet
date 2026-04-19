#!/usr/bin/env python3
"""Grade cheatsheet outputs against assertions."""
import json, re, sys, os

def check_frontmatter(content, require_difficulty=False):
    """Check YAML frontmatter has required fields."""
    if not content.startswith('---'):
        return False, "No frontmatter found"
    end = content.find('---', 3)
    if end == -1:
        return False, "Frontmatter not closed"
    fm = content[3:end]
    required = ['title', 'description', 'tags', 'updated']
    if require_difficulty:
        required.append('difficulty')
    missing = [f for f in required if f + ':' not in fm and f + ' :' not in fm]
    if missing:
        return False, f"Missing fields: {missing}"
    return True, "All required fields present"

def check_toc(content):
    """Check TOC section exists with anchor links."""
    if '## Содержание' not in content and '## содержание' not in content.lower():
        return False, "No '## Содержание' section"
    toc_match = re.search(r'## Содержание\n(.*?)(?=\n## )', content, re.DOTALL)
    if not toc_match:
        return False, "TOC section empty or malformed"
    toc = toc_match.group(1)
    links = re.findall(r'\[.*?\]\(#.*?\)', toc)
    if len(links) < 3:
        return False, f"Only {len(links)} TOC links found"
    return True, f"{len(links)} TOC links found"

def check_useful_links(content):
    """Check 'Полезные ссылки' section with external links."""
    if '## Полезные ссылки' not in content:
        return False, "No '## Полезные ссылки' section"
    links_section = re.search(r'## Полезные ссылки\n(.*?)(?=\n## )', content, re.DOTALL)
    if not links_section:
        return False, "Section empty"
    urls = re.findall(r'https?://', links_section.group(1))
    if len(urls) < 2:
        return False, f"Only {len(urls)} external links"
    return True, f"{len(urls)} external links found"

def check_code_blocks(content, min_count=3, lang='java'):
    """Check minimum code blocks with language specified."""
    blocks = re.findall(r'```' + lang, content)
    if len(blocks) < min_count:
        return False, f"Only {len(blocks)} ```{lang} blocks (need {min_count})"
    return True, f"{len(blocks)} ```{lang} blocks found"

def check_keyword(content, keyword, context_desc):
    """Check content contains keyword/topic."""
    if keyword.lower() in content.lower():
        return True, f"Found '{keyword}'"
    return False, f"'{keyword}' not found — {context_desc}"

def check_russian(content):
    """Check main text is in Russian."""
    russian_chars = len(re.findall(r'[а-яА-ЯёЁ]', content))
    total_alpha = len(re.findall(r'[a-zA-Zа-яА-ЯёЁ]', content))
    if total_alpha == 0:
        return False, "No text found"
    ratio = russian_chars / total_alpha
    if ratio < 0.3:
        return False, f"Russian ratio only {ratio:.1%}"
    return True, f"Russian ratio {ratio:.1%}"

def grade_jmm(filepath):
    """Grade JMM cheatsheet."""
    with open(filepath) as f:
        content = f.read()
    results = []

    passed, ev = check_frontmatter(content)
    results.append({"text": "frontmatter-valid", "passed": passed, "evidence": ev})

    passed, ev = check_toc(content)
    results.append({"text": "toc-present", "passed": passed, "evidence": ev})

    passed, ev = check_useful_links(content)
    results.append({"text": "useful-links", "passed": passed, "evidence": ev})

    # happens-before: check for at least 5 rules
    hb_rules = len(re.findall(r'(?:правило|rule|hb\d|happens.before.*?[:—])', content, re.IGNORECASE))
    hb_section = 'happens-before' in content.lower() or 'happens before' in content.lower()
    if hb_section and hb_rules >= 3:
        results.append({"text": "happens-before", "passed": True, "evidence": f"HB section found with ~{hb_rules} rule mentions"})
    elif hb_section:
        results.append({"text": "happens-before", "passed": True, "evidence": "HB section found"})
    else:
        results.append({"text": "happens-before", "passed": False, "evidence": "No happens-before section"})

    passed, ev = check_keyword(content, 'volatile', 'volatile semantics')
    results.append({"text": "volatile-semantics", "passed": passed, "evidence": ev})

    passed, ev = check_keyword(content, 'final', 'final field semantics')
    final_section = bool(re.search(r'##.*final', content, re.IGNORECASE))
    results.append({"text": "final-fields", "passed": final_section, "evidence": "Dedicated final fields section" if final_section else "No dedicated final fields section"})

    sync_section = bool(re.search(r'##.*synchronized', content, re.IGNORECASE)) or bool(re.search(r'##.*Lock', content))
    results.append({"text": "synchronized-lock", "passed": sync_section, "evidence": "synchronized/Lock sections found" if sync_section else "No synchronized/Lock sections"})

    passed, ev = check_code_blocks(content, 3, 'java')
    results.append({"text": "code-examples", "passed": passed, "evidence": ev})

    passed, ev = check_russian(content)
    results.append({"text": "russian-language", "passed": passed, "evidence": ev})

    # practical value: bugs, antipatterns
    practical = any(kw in content.lower() for kw in ['ошибк', 'антипаттерн', 'баг', 'гонк', 'race', 'типичн'])
    results.append({"text": "practical-value", "passed": practical, "evidence": "Practical examples found" if practical else "No practical examples"})

    return results

def grade_vt(filepath):
    """Grade Virtual Threads cheatsheet."""
    with open(filepath) as f:
        content = f.read()
    results = []

    passed, ev = check_frontmatter(content, require_difficulty=True)
    results.append({"text": "frontmatter-valid", "passed": passed, "evidence": ev})

    passed, ev = check_toc(content)
    results.append({"text": "toc-present", "passed": passed, "evidence": ev})

    passed, ev = check_useful_links(content)
    results.append({"text": "useful-links", "passed": passed, "evidence": ev})

    api_coverage = 'Thread.ofVirtual' in content or 'ofVirtual' in content
    executor_coverage = 'newVirtualThreadPerTaskExecutor' in content
    results.append({"text": "api-coverage", "passed": api_coverage and executor_coverage,
                     "evidence": f"ofVirtual={'yes' if api_coverage else 'no'}, executor={'yes' if executor_coverage else 'no'}"})

    sc = 'StructuredTaskScope' in content
    results.append({"text": "structured-concurrency", "passed": sc, "evidence": "StructuredTaskScope found" if sc else "No StructuredTaskScope"})

    comparison = bool(re.search(r'platform.*thread|platform.*virtual|сравнени', content, re.IGNORECASE))
    results.append({"text": "platform-vs-virtual", "passed": comparison, "evidence": "Comparison found" if comparison else "No comparison"})

    pinning = 'pinning' in content.lower() or 'пиннинг' in content.lower()
    results.append({"text": "pinning", "passed": pinning, "evidence": "Pinning discussed" if pinning else "No pinning discussion"})

    bp = bool(re.search(r'(лучши[ех] практик|best.practice|рекомендаци)', content, re.IGNORECASE))
    results.append({"text": "best-practices", "passed": bp, "evidence": "Best practices section found" if bp else "No best practices"})

    passed, ev = check_code_blocks(content, 3, 'java')
    results.append({"text": "code-examples", "passed": passed, "evidence": ev})

    passed, ev = check_russian(content)
    results.append({"text": "russian-language", "passed": passed, "evidence": ev})

    return results

def save_grading(results, output_path):
    grading = {
        "expectations": results,
        "pass_count": sum(1 for r in results if r["passed"]),
        "total_count": len(results),
        "pass_rate": sum(1 for r in results if r["passed"]) / len(results) if results else 0
    }
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with open(output_path, 'w') as f:
        json.dump(grading, f, indent=2, ensure_ascii=False)
    return grading

base = '/Users/sergeyvoronin/IdeaProjects/cheat-sheet/.claude/skills/cheatsheet-writer-workspace/iteration-1'

configs = [
    ('eval-1-jmm-update', 'with_skill', 'jmm'),
    ('eval-1-jmm-update', 'without_skill', 'jmm'),
    ('eval-2-virtual-threads-new', 'with_skill', 'vt'),
    ('eval-2-virtual-threads-new', 'without_skill', 'vt'),
]

for eval_dir, config, eval_type in configs:
    if eval_type == 'jmm':
        filepath = f'{base}/{eval_dir}/{config}/outputs/java-memory-model.md'
        results = grade_jmm(filepath)
    else:
        filepath = f'{base}/{eval_dir}/{config}/outputs/java-virtual-threads.md'
        results = grade_vt(filepath)

    grading = save_grading(results, f'{base}/{eval_dir}/{config}/grading.json')
    print(f"{eval_dir}/{config}: {grading['pass_count']}/{grading['total_count']} ({grading['pass_rate']:.0%})")
