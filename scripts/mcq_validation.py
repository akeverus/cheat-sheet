#!/usr/bin/env python3
"""MCQ schema/structure/linkage gate. Semantic review is always a separate step.

Exit 0: PASS or explicit empty-staged SKIP; 1: invalid content; 2: execution error.
"""
from __future__ import annotations

import argparse
import json
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SEED = ROOT / "modules/quiz-app/src/main/resources/seed/mcq"
THEORY = ROOT / "cheatsheets/interview"
SCHEMA = SEED.parent / "mcq-schema.json"
QUESTION = re.compile(r"^##\s+Q(\d+)\.?\s*(.*)$")


def _object(pairs):
    result = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"duplicate JSON key: {key}")
        result[key] = value
    return result


def _constant(value):
    raise ValueError(f"non-JSON number: {value}")


def parse_json(text):
    return json.loads(text, object_pairs_hook=_object, parse_constant=_constant)


def read_json(path):
    return parse_json(Path(path).read_text(encoding="utf-8"))


def schema_validator():
    # Один backend: прежде ajv и jsonschema получали несовместимые аргументы.
    from jsonschema import Draft7Validator
    schema = read_json(SCHEMA)
    try:
        Draft7Validator.check_schema(schema)
    except Exception as exc:
        raise ValueError(f"invalid MCQ schema: {exc}") from exc
    return Draft7Validator(schema)


def visible_title(text):
    """Правила MarkdownQuestionParser.normalizeTitle + TextUtils.stripMarkdown."""
    text = re.sub(r"\(важно\)", "", text.replace("(!)", ""), flags=re.IGNORECASE)
    text = re.sub(r"```[a-zA-Z]*\s*", "", text).replace("```", "")
    text = re.sub(r"`([^`]*)`", r"\1", text).replace("`", "")
    for pattern in (r"\*\*(.+?)\*\*", r"\*(.+?)\*", r"__(.+?)__", r"_(.+?)_", r"~~(.+?)~~"):
        text = re.sub(pattern, r"\1", text)
    text = re.sub(r"^#{1,6}\s+", "", text)
    return " ".join(text.replace("*", "").replace("#", "").split())


def question_sections(text):
    """Список сохраняет дубликаты; trim/fence соответствуют Java parser."""
    rows, fenced = [], False
    for line_no, line in enumerate(text.splitlines(), 1):
        stripped = line.strip()
        if stripped.startswith("```"):
            fenced = not fenced
        match = QUESTION.match(stripped) if not fenced else None
        if match:
            rows.append({"q_number": int(match[1]),
                         "title": visible_title(match[2]),
                         "line": line_no, "body": ""})
        elif rows:
            rows[-1]["body"] += line + "\n"
    return rows


def blank_strings(value, at="$"):
    if isinstance(value, str):
        if not value.strip():
            yield f"{at}: blank string"
        if "\x00" in value:
            yield f"{at}: NUL cannot be represented in PostgreSQL text"
    elif isinstance(value, dict):
        for key, item in value.items():
            yield from blank_strings(item, f"{at}.{key}")
    elif isinstance(value, list):
        for idx, item in enumerate(value):
            yield from blank_strings(item, f"{at}[{idx}]")


def validate_seed(path, validator, theory_path=None):
    path = Path(path)
    report = {"path": str(path), "status": "PASS", "errors": [],
              "questions": 0, "blocks": 0, "options": 0, "theory": "NOT_RUN"}
    errors = report["errors"]
    try:
        data = read_json(path)
    except OSError as exc:
        report.update(status="ERROR", errors=[str(exc)])
        return report
    except (ValueError, UnicodeError) as exc:
        report.update(status="FAIL", errors=[str(exc)])
        return report
    for error in validator.iter_errors(data):
        location = "/".join(map(str, error.absolute_path)) or "$"
        errors.append(f"schema {location}: {error.message}")
    if errors:
        report["status"] = "FAIL"
        return report
    errors.extend(blank_strings(data))
    if data["topic_slug"] != path.stem:
        errors.append(f"topic_slug must equal filename: {path.stem}")
    numbers = [q["q_number"] for q in data["questions"]]
    report["questions"] = len(numbers)
    if len(numbers) != len(set(numbers)):
        errors.append("duplicate q_number")
    for question in data["questions"]:
        tag = f"Q{question['q_number']}"
        indices = [b["block_idx"] for b in question["blocks"]]
        if indices != list(range(len(indices))):
            errors.append(f"{tag}: block_idx must be unique, ordered and start at 0")
        for block in question["blocks"]:
            at = f"{tag}/B{block['block_idx']}"
            options = block["options"]
            report["blocks"] += 1
            report["options"] += len(options)
            if sum(o["correct"] is True for o in options) != 1:
                errors.append(f"{at}: exactly one correct option required")
            if [o["order"] for o in options] != [0, 1, 2, 3]:
                errors.append(f"{at}: order must be [0,1,2,3]")
            if [o["label"] for o in options] != list("ABCD"):
                errors.append(f"{at}: labels must be A/B/C/D aligned with order")
            texts = [" ".join(o["text"].split()) for o in options]
            if len(set(texts)) != len(texts):
                errors.append(f"{at}: duplicate option text")
    if theory_path is not None:
        report["theory"] = str(theory_path)
        try:
            sections = question_sections(Path(theory_path).read_text(encoding="utf-8"))
        except (OSError, UnicodeError) as exc:
            errors.append(f"theory unavailable: {exc}")
        else:
            visible = [q["q_number"] for q in sections]
            if len(visible) != len(set(visible)):
                errors.append("duplicate Markdown q_number")
            if any(not q["title"] for q in sections):
                errors.append("blank visible question title")
            if numbers != visible:
                errors.append(f"JSON/Markdown question sequence mismatch: {numbers} != {visible}")
    report["status"] = "FAIL" if errors else "PASS"
    return report


def canonical_theory(path):
    try:
        relative = Path(path).resolve().relative_to(SEED)
    except ValueError:
        return None  # Внешние fixtures: schema/structure; связь явно NOT_RUN.
    return THEORY / relative.with_suffix(".md")


def select_paths(paths, all_files=False, staged=False):
    if all_files:
        selected = sorted(SEED.rglob("*.json"))
    elif staged:
        proc = subprocess.run(
            ["git", "diff", "--cached", "--name-only", "-z", "--diff-filter=ACMR"],
            cwd=ROOT, check=True, capture_output=True)
        selected = [ROOT / p for p in proc.stdout.decode().split("\0")
                    if p.startswith("modules/quiz-app/src/main/resources/seed/mcq/")
                    and p.endswith(".json")]
    else:
        selected = []
        for value in paths:
            path = Path(value).resolve()
            if path.is_dir():
                children = sorted(path.rglob("*.json"))
                if not children:
                    raise ValueError(f"no JSON files in {path}")
                selected.extend(children)
            elif path.is_file() and path.suffix == ".json":
                selected.append(path)
            else:
                raise ValueError(f"JSON file not found: {path}")
    if not selected and not staged:
        raise ValueError("no MCQ files selected")
    return list(dict.fromkeys(selected))


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--all", action="store_true")
    mode.add_argument("--staged", action="store_true", help="staged paths, working-tree content")
    parser.add_argument("--json", action="store_true")
    parser.add_argument("paths", nargs="*")
    args = parser.parse_args(argv)
    if args.paths and (args.all or args.staged):
        parser.error("choose paths OR --all OR --staged")
    try:
        files = select_paths(args.paths, args.all, args.staged)
        if not files:
            print(json.dumps({"status": "SKIP", "files": []}) if args.json
                  else "SKIP: no staged MCQ paths; corpus was not checked")
            return 0
        validator = schema_validator()
    except (ImportError, OSError, ValueError, subprocess.CalledProcessError) as exc:
        print(f"ERROR: {exc}. Dependencies: python3 -m pip install -r scripts/requirements-mcq.txt",
              file=sys.stderr)
        return 2
    reports = [validate_seed(p, validator, canonical_theory(p)) for p in files]
    status = "ERROR" if any(r["status"] == "ERROR" for r in reports) else (
        "FAIL" if any(r["status"] == "FAIL" for r in reports) else "PASS")
    if args.json:
        print(json.dumps({"status": status, "files": reports}, ensure_ascii=False, indent=2))
    else:
        for report in reports:
            if report["errors"]:
                print(f"{report['status']} {report['path']}")
                for error in report["errors"]:
                    print(f"  {error}")
        print(f"{status}: files={len(reports)} questions={sum(r['questions'] for r in reports)} "
              f"blocks={sum(r['blocks'] for r in reports)}; semantic review NOT_RUN")
    return {"PASS": 0, "FAIL": 1, "ERROR": 2}[status]


if __name__ == "__main__":
    sys.exit(main())
