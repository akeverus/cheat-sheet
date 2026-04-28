#!/usr/bin/env python3
"""
Normalize Obsidian frontmatter across cheatsheets/.

Standard contract: see .claude/skills/obsidian-metadata/SKILL.md.

Usage:
  python3 normalize_frontmatter.py --check        # dry run, report
  python3 normalize_frontmatter.py --apply        # write changes
  python3 normalize_frontmatter.py --apply --root cheatsheets/architecture
"""

from __future__ import annotations

import argparse
import datetime as dt
import re
import sys
from collections import OrderedDict
from pathlib import Path
from typing import Iterable

ROOT = Path(__file__).resolve().parents[4] / "cheatsheets"
LOG_PATH = Path(__file__).resolve().parents[1] / "last-run.log"
TODAY = dt.date.today().isoformat()

ALLOWED_TYPES = {"index", "rules", "overview", "reference", "how-to", "troubleshooting", "interview"}
ALLOWED_DIFF = {"beginner", "intermediate", "advanced"}
DIFF_ALIASES = {
    "easy": "beginner",
    "novice": "beginner",
    "newbie": "beginner",
    "medium": "intermediate",
    "intermediate": "intermediate",
    "hard": "advanced",
    "expert": "advanced",
    "senior": "advanced",
    "advanced": "advanced",
    "junior": "beginner",
    "middle": "intermediate",
}

CANONICAL_ORDER = [
    "title",
    "description",
    "tags",
    "type",
    "difficulty",
    "aliases",
    "prerequisites",
    "related",
    "next",
    "cssclasses",
    "cover",
    "updated",
]

LIST_FIELDS = {"tags", "aliases", "prerequisites", "related", "next", "cssclasses"}
WIKILINK_FIELDS = {"prerequisites", "related", "next"}

FM_RE = re.compile(r"^---\n(.*?)\n---\n?", re.DOTALL)


# ---------- minimal YAML reader for our flat shape ----------

def parse_frontmatter(text: str) -> tuple[OrderedDict, int] | tuple[None, int]:
    m = FM_RE.match(text)
    if not m:
        return None, 0
    body = m.group(1)
    fm: OrderedDict = OrderedDict()
    cur_key: str | None = None
    cur_list: list[str] = []
    for raw in body.splitlines():
        if not raw.strip():
            if cur_key and cur_list:
                fm[cur_key] = cur_list
                cur_key, cur_list = None, []
            continue
        # list item
        m_item = re.match(r"^\s+-\s+(.*)$", raw)
        if m_item and cur_key:
            cur_list.append(strip_quotes(m_item.group(1).strip()))
            continue
        # close pending list
        if cur_key:
            fm[cur_key] = cur_list
            cur_key, cur_list = None, []
        # scalar or list start
        m_scalar = re.match(r"^([A-Za-z_][A-Za-z0-9_-]*)\s*:\s*(.*)$", raw)
        if not m_scalar:
            continue
        key = m_scalar.group(1)
        rest = m_scalar.group(2).rstrip()
        if rest == "":
            cur_key = key
            cur_list = []
        elif rest == "[]":
            fm[key] = []
        else:
            # inline JSON-ish list?
            m_inline = re.match(r"^\[(.*)\]$", rest)
            if m_inline:
                items = [s.strip().strip('"').strip("'") for s in m_inline.group(1).split(",") if s.strip()]
                fm[key] = items
            else:
                fm[key] = strip_quotes(rest)
    if cur_key:
        fm[cur_key] = cur_list
    return fm, m.end()


def strip_quotes(s: str) -> str:
    s = s.strip()
    if len(s) >= 2 and s[0] == s[-1]:
        if s[0] == '"':
            inner = s[1:-1]
            # unescape standard YAML double-quoted scalar escapes
            return inner.replace('\\"', '"').replace("\\\\", "\\")
        if s[0] == "'":
            inner = s[1:-1]
            # YAML single-quoted: '' becomes '
            return inner.replace("''", "'")
    return s


# ---------- writer ----------

_NEEDS_QUOTE_RE = re.compile(
    r"""(?x)
    ^\s*$ |                      # empty/whitespace
    ^[\-?:,\[\]\{\}#&*!|>'"%@`] | # leading YAML special
    [:#]\s |                       # colon or hash followed by space
    \s$ |                          # trailing whitespace
    ^(yes|no|true|false|null|~|on|off)$ # YAML reserved scalars
    """,
    re.IGNORECASE,
)


def quote(s: str, force: bool = False) -> str:
    s = str(s)
    if not force and not _NEEDS_QUOTE_RE.search(s):
        return s
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"') + '"'


# String fields that we always quote for stability (titles, descriptions, dates).
ALWAYS_QUOTE_SCALARS = {"title", "description", "updated", "type", "difficulty"}
# List fields whose items we always quote for stability/readability.
ALWAYS_QUOTE_ITEMS = {"aliases", "prerequisites", "related", "next"}
# List fields where empty list is preserved (instead of dropped).
KEEP_EMPTY_LIST = {"prerequisites", "next"}


def emit_frontmatter(fm: OrderedDict) -> str:
    out: list[str] = ["---"]
    keys = [k for k in CANONICAL_ORDER if k in fm] + [k for k in fm if k not in CANONICAL_ORDER]
    for k in keys:
        v = fm[k]
        if isinstance(v, list):
            if not v:
                if k in KEEP_EMPTY_LIST:
                    out.append(f"{k}: []")
                continue
            out.append(f"{k}:")
            force_quote = k in ALWAYS_QUOTE_ITEMS
            for item in v:
                out.append(f"  - {quote(item, force=force_quote)}")
        else:
            force_quote = k in ALWAYS_QUOTE_SCALARS
            out.append(f"{k}: {quote(v, force=force_quote)}")
    out.append("---")
    return "\n".join(out) + "\n"


# ---------- transforms ----------

def to_wikilink(value: str) -> str:
    """Convert 'path/to/file.md' or 'file.md' or 'file' to '[[file]]' (basename, no ext)."""
    v = value.strip()
    # already wikilink?
    if v.startswith("[[") and v.endswith("]]"):
        inner = v[2:-2]
        # normalize inner: drop .md extension
        if inner.lower().endswith(".md"):
            inner = inner[:-3]
        # for cross-folder ambiguity keep relative path; otherwise basename
        if "/" in inner:
            # still keep relative path
            return f"[[{inner}]]"
        return f"[[{inner}]]"
    # treat as path
    if v.lower().endswith(".md"):
        v_no_ext = v[:-3]
    else:
        v_no_ext = v
    base = v_no_ext.split("/")[-1]
    return f"[[{base}]]"


def normalize_tags(tags: list[str]) -> list[str]:
    seen = set()
    out: list[str] = []
    for t in tags:
        t2 = t.strip().lower()
        # convert spaces/underscores to hyphens
        t2 = re.sub(r"[_\s]+", "-", t2)
        # strip leading '#'
        t2 = t2.lstrip("#")
        # collapse multiple hyphens
        t2 = re.sub(r"-+", "-", t2).strip("-")
        if not t2 or t2 in seen:
            continue
        seen.add(t2)
        out.append(t2)
    return out


def infer_type(path: Path, content: str, fm: OrderedDict) -> str:
    cur = fm.get("type")
    if isinstance(cur, str) and cur in ALLOWED_TYPES:
        return cur
    parts = path.parts
    name = path.name
    if "interview" in parts and name not in ("README.md",):
        if name == "TOC.md":
            return "index"
        return "interview"
    if name == "README.md":
        return "index"
    if name == "TOC.md":
        return "index"
    # only one rules file by convention
    if name == "CHEATSHEETS_ARCHITECTURE_AND_RULES.md":
        return "rules"
    # heuristic by content
    lines = content.count("\n")
    lower_name = name.lower()
    if any(s in lower_name for s in ("how-to", "howto", "guide-to")):
        return "how-to"
    if "troubleshoot" in lower_name:
        return "troubleshooting"
    if any(s in lower_name for s in ("overview", "fundamentals", "basics")) and lines >= 200:
        return "overview"
    if lines >= 600:
        return "overview"
    if lines <= 250:
        return "reference"
    return "reference"


def normalize_difficulty(value, type_value: str) -> str | None:
    if type_value in ("index", "rules"):
        return None  # do not set difficulty on these
    if not value:
        return "intermediate"
    v = str(value).strip().lower()
    return DIFF_ALIASES.get(v, "intermediate" if v not in ALLOWED_DIFF else v)


_GENERIC_STEMS = {"readme", "toc", "index"}
_GENERIC_TITLE_PREFIXES = {
    "вопросы на собеседовании",
    "вопросы для собеседования",
    "собеседование",
    "interview",
    "interview questions",
}
MAX_ALIAS_LEN = 30
MAX_ALIASES = 4


def _shortlist(items: list[str]) -> list[str]:
    """Dedup case-insensitive, drop items longer than MAX_ALIAS_LEN, cap count."""
    seen: set[str] = set()
    cleaned: list[str] = []
    for a in items:
        s = str(a).strip()
        if not s or len(s) > MAX_ALIAS_LEN:
            continue
        k = s.lower()
        if k in seen:
            continue
        seen.add(k)
        cleaned.append(s)
        if len(cleaned) >= MAX_ALIASES:
            break
    return cleaned


def auto_aliases(title: str, path: Path) -> list[str]:
    base = path.stem
    out: list[str] = []
    t = title.strip()
    # If "X: Y" — keep short halves only, never the joined long version.
    if ":" in t:
        head, _, tail = t.partition(":")
        for part in (head.strip(), tail.strip()):
            if part:
                out.append(part)
    else:
        out.append(t)
    pretty = base.replace("-", " ").replace("_", " ").strip()
    if pretty and pretty.lower() not in _GENERIC_STEMS:
        out.append(pretty)
    return _shortlist(out)


def normalize_updated(value) -> str:
    if isinstance(value, str):
        v = value.strip()
        if re.match(r"^\d{4}-\d{2}-\d{2}$", v):
            try:
                d = dt.date.fromisoformat(v)
                if d.year < 2020 or d > dt.date(2027, 12, 31):
                    return TODAY
                return v
            except ValueError:
                pass
    return TODAY


# ---------- per-file pipeline ----------

def normalize_file(path: Path) -> tuple[bool, list[str], str | None]:
    """Returns (changed, log_messages, new_text_or_None)."""
    text = path.read_text(encoding="utf-8")
    fm, end = parse_frontmatter(text)
    log: list[str] = []
    if fm is None:
        # No frontmatter — generate minimal scaffold
        log.append("missing frontmatter; scaffolded")
        title = path.stem.replace("-", " ").title()
        fm = OrderedDict()
        fm["title"] = title
        fm["description"] = f"{title} — заметка."
        fm["tags"] = [path.parts[1] if len(path.parts) > 1 else "misc"]
        fm["type"] = "reference"
        fm["updated"] = TODAY
        end = 0

    original = OrderedDict(fm)

    # 1. tags
    if "tags" in fm:
        if isinstance(fm["tags"], str):
            fm["tags"] = [fm["tags"]]
        fm["tags"] = normalize_tags(fm["tags"]) if isinstance(fm["tags"], list) else []
    else:
        fm["tags"] = []
    if not fm["tags"]:
        # derive from folder
        if len(path.parts) > 1:
            fm["tags"] = normalize_tags([path.parts[1]])
            log.append(f"tags derived from folder → {fm['tags']}")

    # 2. type
    new_type = infer_type(path, text, fm)
    if fm.get("type") != new_type:
        log.append(f"type: {fm.get('type')!r} → {new_type!r}")
    fm["type"] = new_type

    # 3. difficulty
    new_diff = normalize_difficulty(fm.get("difficulty"), new_type)
    if new_diff is None:
        if "difficulty" in fm:
            log.append("difficulty removed (not applicable for index/rules)")
            fm.pop("difficulty", None)
    else:
        if fm.get("difficulty") != new_diff:
            log.append(f"difficulty: {fm.get('difficulty')!r} → {new_diff!r}")
        fm["difficulty"] = new_diff

    # 4. aliases — shortlist existing, then auto-generate if (still) empty
    if isinstance(fm.get("aliases"), list):
        cleaned = _shortlist(fm["aliases"])
        if cleaned != fm["aliases"]:
            log.append("aliases shortened/deduped")
        fm["aliases"] = cleaned
    if new_type != "rules" and isinstance(fm.get("title"), str):
        if not fm.get("aliases"):
            a = auto_aliases(fm["title"], path)
            if a:
                fm["aliases"] = a
                log.append(f"aliases generated: {a}")
    if "aliases" in fm and not fm["aliases"]:
        # nothing left and nothing generated — drop empty list to avoid noise
        fm.pop("aliases", None)

    # 5. wikilink fields
    for k in WIKILINK_FIELDS:
        if k in fm:
            v = fm[k]
            if isinstance(v, str):
                v = [v]
            if not isinstance(v, list):
                fm.pop(k, None)
                continue
            if not v:
                if k in KEEP_EMPTY_LIST:
                    fm[k] = []
                else:
                    fm.pop(k, None)
                    log.append(f"{k}: removed empty list")
                continue
            new_v = [to_wikilink(x) for x in v]
            if new_v != v:
                log.append(f"{k}: wikilink-normalized")
            fm[k] = new_v

    # 5b. ensure prerequisites/next exist on every content file (even if empty)
    if new_type not in ("rules",):
        for k in KEEP_EMPTY_LIST:
            if k not in fm:
                fm[k] = []
                log.append(f"{k}: added empty []")

    # 6. updated
    new_updated = normalize_updated(fm.get("updated"))
    if not isinstance(fm.get("updated"), str) or fm.get("updated") != new_updated:
        # Only bump to TODAY if file was actually changed by us; we'll decide later.
        # For now leave existing if it parses as a valid past date.
        if not isinstance(fm.get("updated"), str) or not re.match(r"^\d{4}-\d{2}-\d{2}$", fm["updated"]):
            fm["updated"] = TODAY
            log.append(f"updated → {TODAY}")
    # 7. mandatory fields
    for req in ("title", "description"):
        if req not in fm or not fm[req]:
            if req == "title":
                fm[req] = path.stem.replace("-", " ").strip().capitalize()
            else:
                fm[req] = "TODO: краткое описание шпаргалки."
            log.append(f"{req} added (was missing)")

    # 8. drop deprecated fields
    for dep in ("status", "author", "version", "draft"):
        if dep in fm:
            fm.pop(dep)
            log.append(f"dropped deprecated field {dep}")

    # 9. produce new text and compare verbatim — covers both data changes and emitter-format changes
    new_fm_text = emit_frontmatter(fm)
    rest = text[end:] if end > 0 else ("\n" + text.lstrip())
    new_text = new_fm_text + rest
    if new_text == text:
        return False, log, None
    return True, log, new_text


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="write changes")
    parser.add_argument("--check", action="store_true", help="dry run (default)")
    parser.add_argument("--root", default=str(ROOT), help="root folder")
    parser.add_argument("--limit", type=int, default=0, help="process at most N files (0 = all)")
    parser.add_argument("--quiet", action="store_true")
    args = parser.parse_args(argv)

    root = Path(args.root).resolve()
    files: Iterable[Path] = sorted(root.rglob("*.md"))
    log_lines: list[str] = []
    log_lines.append(f"# normalize_frontmatter run @ {dt.datetime.now().isoformat()}")
    log_lines.append(f"# root={root} apply={args.apply}")
    log_lines.append("")

    total = 0
    changed = 0
    rewrites: list[tuple[Path, str]] = []

    for f in files:
        if args.limit and total >= args.limit:
            break
        total += 1
        try:
            ch, msgs, new_text = normalize_file(f)
        except Exception as e:  # noqa: BLE001
            log_lines.append(f"ERROR {f}: {e}")
            continue
        if not ch:
            continue
        changed += 1
        rel = f.relative_to(root.parent if root.parent.exists() else root)
        log_lines.append(f"-- {rel}")
        for m in msgs:
            log_lines.append(f"   {m}")
        if new_text:
            rewrites.append((f, new_text))

    log_lines.append("")
    log_lines.append(f"# total scanned: {total}")
    log_lines.append(f"# changed: {changed}")

    LOG_PATH.write_text("\n".join(log_lines) + "\n", encoding="utf-8")

    if args.apply:
        for f, new in rewrites:
            f.write_text(new, encoding="utf-8")
        if not args.quiet:
            print(f"applied: {changed}/{total} files; log → {LOG_PATH}")
    else:
        if not args.quiet:
            print(f"dry run: {changed}/{total} files would change; log → {LOG_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
