#!/usr/bin/env python3
"""
Configure Obsidian graph view for the cheatsheets vault.

Updates `.obsidian/graph.json` with sensible defaults:
- color groups by primary section tag (architecture, databases, frameworks, ...)
- showTags = True (groups visible by tag color)
- nodeSizeMultiplier / linkDistance tuned for ~1000 nodes
- preserves user-set forces (centerStrength, repelStrength, etc.)
"""

from __future__ import annotations

import json
import sys
from pathlib import Path

GRAPH_PATH = Path(__file__).resolve().parents[4] / ".obsidian" / "graph.json"

# tag-based color groups: query syntax is Obsidian's "search" — `tag:#foo`
COLOR_GROUPS = [
    ("tag:#architecture",         {"a": 1, "rgb":  16737095}),  # red
    ("tag:#databases",            {"a": 1, "rgb":   3973375}),  # blue
    ("tag:#frameworks",           {"a": 1, "rgb":   3996894}),  # cyan
    ("tag:#libraries",            {"a": 1, "rgb":   8108194}),  # green
    ("tag:#languages",            {"a": 1, "rgb":  16753920}),  # orange
    ("tag:#programming-languages",{"a": 1, "rgb":  16753920}),
    ("tag:#platform",             {"a": 1, "rgb":  10498016}),  # violet
    ("tag:#monitoring",           {"a": 1, "rgb":  16776960}),  # yellow
    ("tag:#testing",              {"a": 1, "rgb":   6724095}),  # teal
    ("tag:#patterns",             {"a": 1, "rgb":  14423100}),  # pink
    ("tag:#security",             {"a": 1, "rgb":  10027008}),  # dark red
    ("tag:#interview",            {"a": 1, "rgb":   9856975}),  # purple
    ("tag:#algorithms",           {"a": 1, "rgb":   3329330}),  # dark green
    ("tag:#tools",                {"a": 1, "rgb":   8421504}),  # gray
    ("tag:#index",                {"a": 1, "rgb":   1118481}),  # almost black
    ("tag:#meta",                 {"a": 1, "rgb":   1118481}),
]


DEFAULTS = {
    "collapse-filter": False,
    "search": "",
    "showTags": True,
    "showAttachments": False,
    "hideUnresolved": False,
    "showOrphans": True,
    "collapse-color-groups": False,
    "collapse-display": False,
    "showArrow": False,
    "textFadeMultiplier": 0.5,
    "nodeSizeMultiplier": 1.4,
    "lineSizeMultiplier": 1,
    "collapse-forces": False,
    # default forces — user can tune in UI; we set sane values for ~1k nodes
    "centerStrength": 0.15,
    "repelStrength": 12,
    "linkStrength": 1,
    "linkDistance": 250,
    "scale": 0.5,
    "close": False,
}


def main() -> int:
    if not GRAPH_PATH.parent.exists():
        print(f"error: {GRAPH_PATH.parent} does not exist; not an Obsidian vault?", file=sys.stderr)
        return 1
    existing: dict = {}
    if GRAPH_PATH.exists():
        try:
            existing = json.loads(GRAPH_PATH.read_text(encoding="utf-8"))
        except json.JSONDecodeError:
            print(f"warning: {GRAPH_PATH} is not valid JSON; rewriting from scratch", file=sys.stderr)
            existing = {}

    merged = {**DEFAULTS, **existing}
    # always overwrite color groups — that's the point of this script
    merged["colorGroups"] = [
        {"query": q, "color": c} for q, c in COLOR_GROUPS
    ]
    # but we keep user's forces if they tuned them
    for k in ("centerStrength", "repelStrength", "linkStrength", "linkDistance",
             "scale", "nodeSizeMultiplier", "lineSizeMultiplier", "textFadeMultiplier"):
        if k in existing:
            merged[k] = existing[k]
    merged["showTags"] = True  # this we force on
    merged["collapse-color-groups"] = False

    GRAPH_PATH.write_text(json.dumps(merged, indent=2, ensure_ascii=False), encoding="utf-8")
    print(f"updated {GRAPH_PATH}")
    print(f"  color groups: {len(merged['colorGroups'])}")
    print(f"  showTags: {merged['showTags']}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
