#!/usr/bin/env python3
"""Прогнать все три прохода автоправок последовательно.

Эквивалент:
    python3 scripts/cheatsheet-autofix.py    cheatsheets
    python3 scripts/cheatsheet-autofix-v2.py cheatsheets
    python3 scripts/cheatsheet-autofix-v3.py cheatsheets
    python3 scripts/cheatsheet-regenerate-toc.py cheatsheets

Единая точка входа для новых контрибьюторов и pre-commit hook.
"""
from __future__ import annotations

import subprocess
import sys
from pathlib import Path

SCRIPTS = [
    "cheatsheet-autofix.py",
    "cheatsheet-autofix-v2.py",
    "cheatsheet-autofix-v3.py",
    "cheatsheet-regenerate-toc.py",
]


def main() -> int:
    root = sys.argv[1] if len(sys.argv) > 1 else "cheatsheets"
    here = Path(__file__).parent
    rc = 0
    for s in SCRIPTS:
        print(f"\n=== {s} ===")
        result = subprocess.run(
            ["python3", str(here / s), root, *sys.argv[2:]],
            check=False,
        )
        if result.returncode != 0:
            print(f"!! {s} exited with code {result.returncode}", file=sys.stderr)
            rc = result.returncode
    return rc


if __name__ == "__main__":
    sys.exit(main())
