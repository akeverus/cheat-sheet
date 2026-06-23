#!/usr/bin/env python3
"""Детерминированный аудит switchable-design токенов (regression-гейт).

Парсит static/css/tokens.css, разрешает ЭФФЕКТИВНОЕ значение каждого токена для
каждой пары (design × theme) по реальному CSS-каскаду
(:root -> editorial-dark -> design-light -> design-dark), композитит rgba()
поверх фона и считает WCAG-контраст для всех пар «текст/иконка/кольцо × фон»,
которые реально рендерятся в UI. Плюс проверяет:
  [1] hard-fails (body-текст < 4.5:1),
  [2] large-text/label/focus-ring shortfall (< 3.0:1, вкл. WCAG 1.4.11 для колец),
  [3] полноту dark-блоков (каждый dark обязан переопределить полный цветовой
      набор editorial-dark — иначе утечёт чужой/clay-цвет),
  [4] hardcoded-цвета в base.css вне @media print.

Запуск из корня репо:  python3 scripts/design-token-audit.py
Exit 0 = чисто; любой hard-fail/completeness-issue => ненулевой exit.

История: инструмент нашёл 3 реальных AA/a11y-бага (notion/mintlify success-green
2.93/3.61:1; mintlify-light focus-ring mint 1.7:1) — держи как гейт перед правкой
tokens.css. Дополняет ui/TemplateFragmentContractTest (тот сторожит структуру)."""
import re, sys
from pathlib import Path

TOKENS = Path("modules/quiz-app/src/main/resources/static/css/tokens.css").read_text()
BASE = Path("modules/quiz-app/src/main/resources/static/css/base.css").read_text()

# ---- block extraction --------------------------------------------------------
def block_body(selector_regex):
    """Return the {...} body for the first selector matching selector_regex."""
    m = re.search(selector_regex + r"\s*\{", TOKENS)
    if not m:
        return None
    i = m.end()
    depth = 1
    start = i
    while i < len(TOKENS) and depth:
        c = TOKENS[i]
        if c == "{": depth += 1
        elif c == "}": depth -= 1
        i += 1
    return TOKENS[start:i-1]

def parse_tokens(body):
    out = {}
    if not body: return out
    for tm in re.finditer(r"(--[\w-]+)\s*:\s*([^;]+);", body):
        out[tm.group(1).strip()] = tm.group(2).strip()
    return out

root = parse_tokens(block_body(r":root"))
ed_dark = parse_tokens(block_body(r"\[data-theme=\"dark\"\]"))

DESIGNS = ["editorial", "linear", "swiss", "notion", "mintlify", "broadsheet"]
design_light, design_dark = {}, {}
for d in DESIGNS:
    if d == "editorial":
        continue
    design_light[d] = parse_tokens(block_body(r"html\[data-design=\"%s\"\]" % d))
    design_dark[d] = parse_tokens(block_body(r"html\[data-design=\"%s\"\]\[data-theme=\"dark\"\]" % d))

def effective(design, theme):
    """Cascade (low->high priority): :root -> editorial-dark -> design-light -> design-dark."""
    m = dict(root)
    if theme == "dark":
        m.update(ed_dark)
    if design != "editorial":
        m.update(design_light[design])
        if theme == "dark":
            m.update(design_dark[design])
    return m

# ---- color math --------------------------------------------------------------
def parse_color(v, tokens, bg_for_alpha=None, _depth=0):
    """Return (r,g,b) 0-255 opaque. Composites rgba over bg_for_alpha.
    Resolves var(--x) recursively."""
    v = v.strip()
    if _depth > 8: return None
    mvar = re.match(r"var\(\s*(--[\w-]+)\s*\)", v)
    if mvar:
        ref = tokens.get(mvar.group(1))
        return parse_color(ref, tokens, bg_for_alpha, _depth+1) if ref else None
    m = re.match(r"#([0-9a-fA-F]{3})$", v)
    if m:
        h = m.group(1)
        return tuple(int(h[i]*2, 16) for i in range(3))
    m = re.match(r"#([0-9a-fA-F]{6})$", v)
    if m:
        h = m.group(1)
        return tuple(int(h[i:i+2], 16) for i in (0,2,4))
    m = re.match(r"rgba?\(([^)]+)\)", v)
    if m:
        parts = [p.strip() for p in m.group(1).replace("/", ",").split(",")]
        r,g,b = (float(parts[0]), float(parts[1]), float(parts[2]))
        a = float(parts[3]) if len(parts) > 3 else 1.0
        if a < 1.0 and bg_for_alpha is not None:
            br,bg_,bb = bg_for_alpha
            r = r*a + br*(1-a); g = g*a + bg_*(1-a); b = b*a + bb*(1-a)
        return (r,g,b)
    return None

def lum(c):
    def chan(x):
        x /= 255.0
        return x/12.92 if x <= 0.03928 else ((x+0.055)/1.055)**2.4
    return 0.2126*chan(c[0]) + 0.7152*chan(c[1]) + 0.0722*chan(c[2])

def contrast(fg, bg):
    if fg is None or bg is None: return None
    l1, l2 = lum(fg), lum(bg)
    hi, lo = max(l1,l2), min(l1,l2)
    return (hi+0.05)/(lo+0.05)

# ---- pairings that actually render as text -----------------------------------
# (fg_token, bg_token, min_ratio, label). bg may itself be rgba -> composited over bg-primary.
PAIRINGS = [
    ("--color-text-primary",   "--color-bg-primary",   4.5, "body text / page"),
    ("--color-text-primary",   "--color-bg-secondary", 4.5, "body text / card"),
    ("--color-text-primary",   "--color-bg-tertiary",  4.5, "text / input-well"),
    ("--color-text-secondary", "--color-bg-primary",   4.5, "secondary text / page"),
    ("--color-text-secondary", "--color-bg-secondary", 4.5, "secondary text / card"),
    ("--color-text-tertiary",  "--color-bg-primary",   3.0, "muted label / page (AA-large)"),
    ("--color-text-link",      "--color-bg-primary",   4.5, "link / page"),
    ("--color-text-link",      "--color-bg-secondary", 4.5, "link / card"),
    ("--color-accent-strong",  "--color-bg-primary",   3.0, "accent-as-text / page (eyebrow,AA-large)"),
    ("--color-accent-strong",  "--color-bg-secondary", 3.0, "accent-as-text / card (AA-large)"),
    ("--color-accent-on",      "--color-accent-primary", 4.5, "label on accent-fill (button)"),
    ("--color-status-success", "--color-bg-primary",   3.0, "verdict-ok heading / page (AA-large)"),
    ("--color-status-success", "--color-status-success-wash", 3.0, "verdict-ok heading / wash"),
    ("--color-text-primary",   "--color-status-success-wash", 4.5, "verdict-ok body / wash"),
    ("--color-status-error",   "--color-bg-primary",   3.0, "verdict-err heading / page (AA-large)"),
    ("--color-status-error",   "--color-status-error-wash", 3.0, "verdict-err heading / wash"),
    ("--color-text-primary",   "--color-status-error-wash", 4.5, "verdict-err body / wash"),
    ("--color-status-success-on", "--color-status-success", 4.5, "text on success-fill"),
    ("--color-status-error-on",   "--color-status-error",   4.5, "text on error-fill"),
    ("--color-status-warning", "--color-bg-primary",   3.0, "warning / page (AA-large)"),
    ("--color-status-info",    "--color-bg-primary",   3.0, "info / page (AA-large)"),
    # WCAG 1.4.11 non-text contrast: focus ring vs adjacent surfaces must be >=3:1
    ("--color-border-focus",   "--color-bg-primary",   3.0, "focus-ring / page (WCAG 1.4.11)"),
    ("--color-border-focus",   "--color-bg-secondary", 3.0, "focus-ring / card (WCAG 1.4.11)"),
]

def bg_rgb(token, tokens):
    """Resolve a bg token to opaque rgb, compositing rgba washes over bg-primary."""
    base = parse_color(tokens.get("--color-bg-primary"), tokens)
    return parse_color(tokens.get(token), tokens, bg_for_alpha=base)

fails, warns, checks = [], [], 0
for d in DESIGNS:
    for theme in ("light", "dark"):
        t = effective(d, theme)
        for fg_t, bg_t, minr, label in PAIRINGS:
            bg = bg_rgb(bg_t, t)
            fg = parse_color(t.get(fg_t), t, bg_for_alpha=bg)
            r = contrast(fg, bg)
            if r is None:
                fails.append(f"{d}/{theme}: UNRESOLVED {fg_t} on {bg_t} ({label})")
                continue
            checks += 1
            if r < minr:
                # distinguish hard body-text fail (4.5) from large-text shortfall
                sev = fails if minr >= 4.5 else warns
                sev.append(f"{d}/{theme}: {r:4.2f}:1 (<{minr}) {label}  [{fg_t} on {bg_t}]")

# ---- completeness: every design-dark must redefine editorial-dark color set --
COLOR_TOKENS = [k for k in ed_dark if k.startswith("--color-")]
comp_issues = []
for d in DESIGNS:
    if d == "editorial": continue
    missing = [k for k in COLOR_TOKENS if k not in design_dark[d]]
    for k in missing:
        # what value does it fall back to? design-light, else editorial-dark(=clay leak)
        if k in design_light[d]:
            comp_issues.append(f"{d}/dark: '{k}' not in dark block -> falls to design-LIGHT value {design_light[d][k]} (light color in dark theme?)")
        else:
            comp_issues.append(f"{d}/dark: '{k}' not in dark block -> falls to EDITORIAL-DARK {ed_dark[k]}  *** clay/editorial leak ***")

# ---- base.css purity: hardcoded colors outside var()/comments ----------------
# strip comments first
base_nc = re.sub(r"/\*.*?\*/", "", BASE, flags=re.S)
hard = []
for m in re.finditer(r"#[0-9a-fA-F]{3,8}\b", base_nc):
    hard.append(m.group(0))
# rgb/rgba literals with numeric channels (allow rgba(...) only if inside var fallback? report all)
rgbs = re.findall(r"rgba?\([0-9][^)]*\)", base_nc)

print("="*78)
print("TOKEN AUDIT — WCAG contrast + dark-completeness + base.css purity")
print("="*78)
print(f"Designs: {DESIGNS}")
print(f"Pairings checked: {checks} ({len(PAIRINGS)} per design-theme x {len(DESIGNS)*2})")
print()
print(f"[1] HARD FAILS (body text < 4.5:1): {len(fails)}")
for f in fails: print("   FAIL  " + f)
print()
print(f"[2] LARGE-TEXT SHORTFALL (< 3.0:1 on a large/label pairing): {len(warns)}")
for w in warns: print("   WARN  " + w)
print()
print(f"[3] DARK-BLOCK COMPLETENESS issues: {len(comp_issues)}")
for c in comp_issues: print("   MISS  " + c)
print()
print(f"[4] base.css hardcoded hex colors (outside comments): {len(hard)}")
if hard:
    from collections import Counter
    for col, n in Counter(hard).most_common():
        print(f"   HEX   {col}  x{n}")
print(f"    base.css rgb/rgba literals: {len(rgbs)}")
if rgbs:
    from collections import Counter
    for col, n in Counter(rgbs).most_common(20):
        print(f"   RGB   {col}  x{n}")
print()
verdict = "CLEAN" if not fails and not comp_issues else "ISSUES FOUND"
print(f"VERDICT: {verdict}  (hard-fails={len(fails)}, completeness={len(comp_issues)}, large-warn={len(warns)})")
sys.exit(0 if (not fails and not comp_issues) else 1)
