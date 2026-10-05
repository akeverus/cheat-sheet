#!/usr/bin/env python3
"""Переносимый MCQ workflow без вызовов LLM и платных API. --help: команды.

0 — операция выполнена (не означает приёмку корпуса); 1 — --require-complete не выполнен;
2 — неправильный ввод, устаревший пакет или ошибка. Все пути относительно репозитория.
"""
from __future__ import annotations

import argparse
from collections import Counter
from datetime import datetime, timezone
import importlib.util
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile
import shutil

import mcq_validation as mcq
import mcq_evidence as ev

ROOT = mcq.ROOT
QUALITY = "docs/mcq-quality"
SEED = "modules/quiz-app/src/main/resources/seed/mcq"
spec = importlib.util.spec_from_file_location("mcq_audit", ROOT / "scripts/mcq-audit.py")
audit = importlib.util.module_from_spec(spec)
spec.loader.exec_module(audit)


def now():
    return datetime.now(timezone.utc).isoformat()


def inside(value):
    path = Path(value)
    path = (path if path.is_absolute() else ROOT / path).resolve()
    if not path.is_relative_to(ROOT.resolve()):
        raise ValueError(f"path outside repository: {value}")
    return path


def rel(path):
    return inside(path).relative_to(ROOT.resolve()).as_posix()


def safe_id(value):
    if not re.fullmatch(r"[a-zA-Z0-9][a-zA-Z0-9_.-]{0,119}", value):
        raise ValueError("id: 1..120 letters/digits/underscore/dot/hyphen")
    return value


def write(path, value, replace=False, raw=False):
    """Публикует готовый файл; существующий evidence никогда не перезаписывается."""
    path = inside(path)
    path.parent.mkdir(parents=True, exist_ok=True)
    text = value if raw else json.dumps(value, ensure_ascii=False, indent=2) + "\n"
    fd, name = tempfile.mkstemp(prefix=".mcq-", dir=path.parent)
    try:
        with os.fdopen(fd, "w", encoding="utf-8") as stream:
            stream.write(text)
            stream.flush()
            os.fsync(stream.fileno())
        if replace:
            os.replace(name, path)
        else:
            os.link(name, path)
    finally:
        if os.path.exists(name):
            os.unlink(name)
    return rel(path)


def rules():
    return {p: ev.sha256(inside(p)) for p in ev.RULE_PATHS}


def records():
    result = ev.load_records(ROOT / QUALITY / "reviews/checks")
    for record in result:
        record["_evidence_path"] = rel(record["_evidence_path"])
        for path in record["paths"].values():
            if not inside(path).is_file():
                raise ValueError(f"missing evidence dependency: {path}")
    return result


def seed_path(topic):
    path = inside(topic if str(topic).startswith(SEED + "/") or Path(topic).is_absolute()
                  else str(Path(SEED) / topic))
    if not path.is_relative_to(ROOT / SEED) or path.suffix != ".json" or not path.is_file():
        raise ValueError(f"unknown production topic: {topic}")
    return path


def topic_state(topic, loaded=None, validator=None):
    path = seed_path(topic)
    theory = ROOT / "cheatsheets/interview" / path.relative_to(ROOT / SEED).with_suffix(".md")
    before = {"mcq": ev.sha256(path), "theory": ev.sha256(theory)}
    data, markdown = mcq.read_json(path), theory.read_text(encoding="utf-8")
    checked = mcq.validate_seed(path, validator or mcq.schema_validator(), theory)
    if checked["status"] != "PASS":
        raise ValueError(f"{rel(path)}: fix SCHEMA first: {checked['errors']}")
    hashes = audit.block_hashes(data, markdown)
    legacy_path = ROOT / QUALITY / "audit-evidence.json"
    legacy = mcq.read_json(legacy_path) if legacy_path.exists() else {}
    row = {"id": path.relative_to(ROOT / SEED).as_posix(), "mcq": rel(path), "theory": rel(theory),
           "sha256": before, "all_block_ids": list(hashes), "block_hashes": hashes,
           "questions": len(data["questions"]), "blocks": len(hashes), "structure": "PASS",
           "open_issues": [x for x in legacy.get("open_issues", []) if x["mcq"] == rel(path)
                           and x.get("status") not in {"CLOSED", "RESOLVED"}],
           "runtime_findings": [x for x in legacy.get("runtime_findings", []) if x["mcq"] == rel(path)]}
    row["checks"] = ev.evaluate(row, records() if loaded is None else loaded, hashes, rules(), ROOT)
    if before != {"mcq": ev.sha256(path), "theory": ev.sha256(theory)}:
        raise ValueError("content changed during read; retry after the writer finishes")
    return row, data, markdown


def live_rows(topic=None):
    loaded, validator = records(), mcq.schema_validator()
    manifest = mcq.read_json(ROOT / QUALITY / "linkage-reset-manifest.json")
    claims = {x["mcq_file"]: x["status"] for x in manifest["files"]}
    paths = [seed_path(topic)] if topic else sorted((ROOT / SEED).rglob("*.json"))
    if not paths:
        raise ValueError("empty production corpus")
    result = []
    for path in paths:
        row, _, _ = topic_state(str(path), loaded, validator)
        row["stage"] = "CURRENT_UPDATED_SET" if claims.get(row["mcq"]) == "AUDITED" else "TODO_AFTER_UPDATED_SET"
        result.append(row)
    return sorted(result, key=lambda r: (r["stage"] != "CURRENT_UPDATED_SET", r["id"]))


def summary(rows):
    return {"files": len(rows), "blocks": sum(r["blocks"] for r in rows),
            "accepted_files": sum(r["checks"]["final_acceptance"]["status"] == "PASS" for r in rows),
            "accepted_blocks": sum(r["checks"]["final_acceptance"]["current_blocks"] for r in rows),
            "checks": {c: {"passed_blocks": sum(r["checks"][c]["current_blocks"] for r in rows),
                            "remaining_blocks": sum(len(r["checks"][c]["remaining_blocks"]) for r in rows),
                            "file_statuses": dict(Counter(r["checks"][c]["status"] for r in rows))}
                       for c in ev.CHECKS}}


def next_batch(rows, size=4):
    if not 1 <= size <= 20:
        raise ValueError("batch size must be 1..20")
    deferred = []
    for row in rows:
        check = row["checks"]["final_acceptance"]
        remaining = check["remaining_blocks"]
        if not remaining:
            continue
        runnable = [b for b in remaining if check["block_statuses"].get(b, {}).get("status") != "BLOCKED"]
        if not runnable:
            deferred.append({"topic": row["id"], "blocks": remaining})
            continue
        selected = runnable[:size]
        return {"topic": row["id"], "stage": row["stage"], "blocks": selected,
                "checks": {c: [b for b in selected if b in v["remaining_blocks"]]
                           for c, v in row["checks"].items() if any(b in v["remaining_blocks"] for b in selected)},
                "deferred_blocked_topics": deferred}
    return {"topic": None, "blocks": [], "deferred_blocked_topics": deferred,
            "complete": not any(r["checks"]["final_acceptance"]["remaining_blocks"] for r in rows)}


def scope(row, value):
    ids = value.split(",") if isinstance(value, str) else value
    if not ids or len(ids) != len(set(ids)) or not set(ids).issubset(row["all_block_ids"]):
        raise ValueError("blocks: exact existing unique Qn:Bn separated by commas (no ranges)")
    return [b for b in row["all_block_ids"] if b in ids]


def payload(row, data, markdown, ids):
    sections = mcq.question_sections(markdown)
    visible = {q["q_number"]: q for q in sections}
    selected = [{"q_number": q["q_number"], "block_idx": b["block_idx"],
                 "visible_question": visible[q["q_number"]]["title"], "block": b}
                for q in data["questions"] for b in q["blocks"]
                if f"Q{q['q_number']}:B{b['block_idx']}" in ids]
    return {"paths": {k: row[k] for k in ("mcq", "theory")}, "sha256": row["sha256"],
            "rules_sha256": rules(), "block_hashes": {b: row["block_hashes"][b] for b in ids},
            "preamble": "\n".join(markdown.splitlines()[:sections[0]["line"] - 1]),
            "theory": [q for q in sections if q["q_number"] in {x["q_number"] for x in selected}],
            "blocks": selected, "open_issues": row["open_issues"], "runtime_findings": row["runtime_findings"],
            "checks": {c: {"blocks": {b: v["block_statuses"].get(b, {"status": "NOT_RUN"}) for b in ids},
                           "open_findings": v["open_findings"]} for c, v in row["checks"].items()},
            "instructions": "Все A/B/C/D и sections. SFPL, ANSC, RU — отдельные вердикты. "
                            "Дополнительные смысловые зависимости прочитать и указать в evidence. "
                            "Сигналы не являются редакторской приёмкой."}


def packet(topic, blocks, output):
    row, data, md = topic_state(topic)
    return {"packet": write(output, payload(row, data, md, scope(row, blocks)))}


def run_dir(run_id):
    return ROOT / QUALITY / "runs" / safe_id(run_id)


def prepare(topic, blocks, run_id):
    row, data, md = topic_state(topic)
    ids = scope(row, blocks)
    pack = payload(row, data, md, ids)
    directory = run_dir(run_id)
    directory.mkdir(parents=True, exist_ok=False)
    blind = [{"file": row["mcq"], "q_number": b["q_number"], "block_idx": b["block_idx"],
              "visible_question": b["visible_question"],
              "options": [{"label": o["label"], "text": o["text"]} for o in b["block"]["options"]]}
             for b in pack["blocks"]]
    input_path = directory / "blind-input.jsonl"
    write(input_path, "".join(json.dumps(b, ensure_ascii=False) + "\n" for b in blind), raw=True)
    manifest = {"schema_version": 1, "run_id": run_id, "created_at": now(),
                "paths": pack["paths"], "sha256": pack["sha256"], "rules_sha256": pack["rules_sha256"],
                "block_hashes": pack["block_hashes"], "blocks": ids,
                "input": {"path": rel(input_path), "sha256": ev.sha256(input_path)}}
    write(directory / "manifest.json", manifest)
    return {"run": run_id, "blind_input": rel(input_path), "blocks": ids,
            "next": "Новый независимый контекст читает только blind_input. Затем phase-a, затем reveal."}


def current_run(run_id):
    directory = run_dir(run_id)
    manifest = mcq.read_json(directory / "manifest.json")
    row, data, md = topic_state(manifest["paths"]["mcq"])
    scope(row, manifest["blocks"])
    if (manifest["paths"]["theory"] != row["theory"] or manifest["rules_sha256"] != rules()
            or any(manifest["block_hashes"][b] != row["block_hashes"][b] for b in manifest["blocks"])
            or not ev.artifact_current(manifest["input"], ROOT)):
        raise ValueError("STALE packet: changed blocks/rules/input; prepare a new run")
    # Проверяется и содержимое экспорта: простого обновления SHA в manifest недостаточно.
    pack = payload(row, data, md, manifest["blocks"])
    expected = [{"file": row["mcq"], "q_number": b["q_number"], "block_idx": b["block_idx"],
                 "visible_question": b["visible_question"],
                 "options": [{"label": o["label"], "text": o["text"]} for o in b["block"]["options"]]}
                for b in pack["blocks"]]
    actual = [mcq.parse_json(line) for line in inside(manifest["input"]["path"]).read_text().splitlines()]
    if actual != expected:
        raise ValueError("blind input mismatch or leaked key")
    return directory, manifest, row, pack


def phase_a(run_id, answers_path, reviewer):
    directory, manifest, _, _ = current_run(run_id)
    if (directory / "disclosure").exists():
        raise ValueError("key already revealed; phase A cannot be repeated in this run")
    answers = mcq.read_json(inside(answers_path))
    if not isinstance(answers, list) or len(answers) != len(manifest["blocks"]):
        raise ValueError("answers must be a list covering every exported Q/B exactly once")
    if {ev.block_id(a) for a in answers} != set(manifest["blocks"]):
        raise ValueError("answers contain missing/duplicate/extra Q/B")
    for answer in answers:
        if (answer.get("choice") not in {"A", "B", "C", "D", "AMBIGUOUS", "NO_VALID_OPTION", "UNSURE"}
                or not ev.nonempty(answer.get("reason"))
                or not isinstance(answer.get("form_signals"), list)):
            raise ValueError("each answer requires choice, concise reason and form_signals list")
    if not ev.nonempty(reviewer):
        raise ValueError("reviewer required")
    value = {"kind": "blind_phase_a", "reviewer": reviewer, "recorded_at": now(),
             "input": manifest["input"], "answers": answers}
    phase_path, receipt_path = directory / "phase-a.json", directory / "phase-a-receipt.json"
    if receipt_path.exists():
        raise ValueError("phase A already sealed")
    if phase_path.exists():
        previous = mcq.read_json(phase_path)
        if any(previous.get(k) != value[k] for k in ("kind", "reviewer", "input", "answers")):
            raise ValueError("cannot change partially saved phase A; create a new run")
    else:
        write(phase_path, value)
    write(receipt_path, {"phase_a_sha256": ev.sha256(phase_path), "sealed_at": now(), "reviewer": reviewer})
    return {"phase_a": rel(phase_path), "receipt": rel(receipt_path)}


def reveal(run_id, author, reviewer, model=None):
    directory, manifest, row, pack = current_run(run_id)
    phase_path = directory / "phase-a.json"
    phase = mcq.read_json(phase_path)
    seal = mcq.read_json(directory / "phase-a-receipt.json")
    if ev.sha256(phase_path) != seal["phase_a_sha256"] or phase["reviewer"] != seal["reviewer"]:
        raise ValueError("phase A changed after sealing")
    if reviewer != phase["reviewer"] or not ev.nonempty(author) or author == reviewer:
        raise ValueError("independent reviewer must match phase A and differ from author")
    at = now()
    if ev.timestamp(phase["recorded_at"]) >= ev.timestamp(at):
        raise ValueError("phase A must precede reveal")
    draft = {"schema_version": 1, "review_id": run_id, "reviewer": reviewer, "author": author,
             "model": model, "date": at, "kind": "independent_two_phase", "paths": pack["paths"],
             "sha256": row["sha256"], "rules_sha256": rules(),
             "phase_a": {"path": rel(phase_path), "sha256": ev.sha256(phase_path)}, "revealed_at": at,
             "coverage": [{"q_number": int(b.split(":")[0][1:]), "block_idx": int(b.split(":")[1][1:]),
                           "sha256": row["block_hashes"][b], "checks": {}} for b in manifest["blocks"]],
             "findings": [], "sources": [], "limitations": [],
             "workflow_run": {"path": rel(directory / "manifest.json"),
                              "sha256": ev.sha256(directory / "manifest.json")}}
    # Три файла раскрываются одновременно; сбой не оставляет receipt без пакета.
    disclosure = directory / "disclosure"
    if disclosure.exists():
        raise ValueError("already revealed; use existing disclosure, do not repeat phase A")
    staging = Path(tempfile.mkdtemp(prefix=".disclosure-", dir=directory))
    try:
        write(staging / "reveal.json", {"revealed_at": at, "reviewer": reviewer,
                                       "phase_a_sha256": ev.sha256(phase_path),
                                       "phase_a_receipt_sha256": ev.sha256(directory / "phase-a-receipt.json")})
        write(staging / "review-input.json", pack)
        write(staging / "evidence-draft.json", draft)
        os.rename(staging, disclosure)
    finally:
        if staging.exists():
            shutil.rmtree(staging)
    return {"review_input": rel(disclosure / "review-input.json"), "draft": rel(disclosure / "evidence-draft.json"),
            "next": "Заполнить только реально выполненные checks, причины, sources/findings и дату; record --input."}


def record_result(input_path):
    record = mcq.read_json(inside(input_path))
    if any(key.startswith("_") for key in record):
        raise ValueError("internal fields are not allowed")
    ev.validate_record(record)
    safe_id(record["review_id"])
    loaded = records()
    if any(r["review_id"] == record["review_id"] for r in loaded):
        raise ValueError("review_id already registered; append a new review, never overwrite")
    row, _, _ = topic_state(record["paths"]["mcq"], loaded)
    if record["sha256"] != row["sha256"] or record["rules_sha256"] != rules():
        raise ValueError("STALE full input snapshot; verify changes and create a current result")
    if ev.timestamp(record["date"]) > datetime.now(timezone.utc):
        raise ValueError("review date is in the future")
    for item in record["coverage"]:
        if item["sha256"] != row["block_hashes"].get(ev.block_id(item)):
            raise ValueError("STALE or unknown Q/B")
        if any(v["status"] == "NOT_RUN" for v in item["checks"].values()):
            raise ValueError("omit checks that were not run; do not overwrite previous evidence with NOT_RUN")
    if record["kind"] == "independent_two_phase" and not record.get("workflow_run"):
        raise ValueError("new two-phase results require workflow_run; historical evidence remains readable")
    if record.get("workflow_run"):
        if not ev.artifact_current(record["workflow_run"], ROOT):
            raise ValueError("workflow manifest changed/missing")
        directory = inside(record["workflow_run"]["path"]).parent
        reveal_data = mcq.read_json(directory / "disclosure/reveal.json")
        if (record.get("revealed_at") != reveal_data["revealed_at"]
                or record.get("phase_a", {}).get("sha256") != reveal_data["phase_a_sha256"]
                or ev.sha256(directory / "phase-a-receipt.json") != reveal_data["phase_a_receipt_sha256"]
                or record["reviewer"] != reveal_data["reviewer"]):
            raise ValueError("phase A/reveal receipt mismatch")
    evaluated = ev.evaluate(row, [*loaded, record], row["block_hashes"], rules(), ROOT)
    for item in record["coverage"]:
        for check, verdict in item["checks"].items():
            effective = evaluated[check]["block_statuses"].get(ev.block_id(item), {}).get("status", "NOT_RUN")
            if verdict["status"] == "PASS" and effective != "PASS":
                raise ValueError(f"unconfirmed PASS {ev.block_id(item)} {check}: {effective}")
            if effective == "STALE" and verdict["status"] != "STALE":
                raise ValueError("STALE dependency/artifact; result not imported")
    dest = ROOT / QUALITY / "reviews/checks" / (record["review_id"] + ".json")
    return {"evidence": write(dest, record), "blocks": [ev.block_id(x) for x in record["coverage"]],
            "next": "status --topic; затем checkpoint; sync на контрольной точке"}


def checkpoint(next_action=None):
    rows = live_rows()
    value = {"schema_version": 1, "generated_at": now(),
             "head": subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip(),
             "summary": summary(rows), "next_batch": next_batch(rows),
             "next_action": next_action or "Продолжить next_batch по PROMPT_PLAN_INTERVIEW.md; сначала проверить актуальность.",
             "sources_of_truth": [QUALITY + "/reviews/checks", QUALITY + "/audit-evidence.json"],
             "rules_sha256": rules(), "usage": {"task_tokens": None, "reason": "Инструмент не измеряет расход LLM."}}
    return {"checkpoint": write(ROOT / QUALITY / "checkpoint.json", value, replace=True),
            "summary": value["summary"], "next_batch": value["next_batch"]}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("doctor", help="Проверить зависимости и evidence без LLM")
    status = sub.add_parser("status", help="Свежие компактные статусы; без чтения audit-state")
    status.add_argument("--topic")
    nxt = sub.add_parser("next", help="Следующий непринятый пакет; AUDITED перед TODO")
    nxt.add_argument("--size", type=int, default=4)
    pkt = sub.add_parser("packet", help="Контекст автору до правок (с ключом)")
    prep = sub.add_parser("prepare", help="Снимок ПОСЛЕ правок для независимого слепого ревью")
    for p in (pkt, prep):
        p.add_argument("--topic", required=True)
        p.add_argument("--blocks", required=True)
    pkt.add_argument("--output", required=True)
    prep.add_argument("--run", required=True)
    pha = sub.add_parser("phase-a", help="Неизменяемо зафиксировать ответы ДО раскрытия")
    pha.add_argument("--run", required=True)
    pha.add_argument("--answers", required=True)
    pha.add_argument("--reviewer", required=True)
    rev = sub.add_parser("reveal", help="Раскрыть теорию/ключ и выдать черновик evidence")
    for arg in ("run", "author", "reviewer"):
        rev.add_argument("--" + arg, required=True)
    rev.add_argument("--model")
    rec = sub.add_parser("record", help="Проверить и добавить готовый evidence; без перезаписи")
    rec.add_argument("--input", required=True)
    cp = sub.add_parser("checkpoint", help="Сохранить переносимую точку продолжения")
    cp.add_argument("--next-action")
    sync = sub.add_parser("sync", help="Перегенерировать общий снимок и таблицу")
    sync.add_argument("--require-complete", action="store_true")
    args = parser.parse_args()
    try:
        if args.command == "doctor":
            mcq.schema_validator()
            loaded = records()
            for p in (*ev.RULE_PATHS, "PLAN_INTERVIEW.md", "PROMPT_PLAN_INTERVIEW.md"):
                if not inside(p).is_file():
                    raise ValueError(f"missing required file: {p}")
            result = {"status": "READY", "root": str(ROOT), "evidence_records": len(loaded),
                      "note": "Готовность инструментов; не семантическая приёмка. status проверяет актуальность."}
        elif args.command in {"status", "next"}:
            rows = live_rows(getattr(args, "topic", None))
            result = next_batch(rows, args.size) if args.command == "next" else {"summary": summary(rows)}
            if getattr(args, "topic", None):
                result["file"] = rows[0]
        elif args.command == "packet":
            result = packet(args.topic, args.blocks, args.output)
        elif args.command == "prepare":
            result = prepare(args.topic, args.blocks, args.run)
        elif args.command == "phase-a":
            result = phase_a(args.run, args.answers, args.reviewer)
        elif args.command == "reveal":
            result = reveal(args.run, args.author, args.reviewer, args.model)
        elif args.command == "record":
            result = record_result(args.input)
        elif args.command == "checkpoint":
            result = checkpoint(args.next_action)
        else:
            command = [sys.executable, str(ROOT / "scripts/mcq-audit.py"), "--output",
                       str(ROOT / QUALITY / "audit-state.json"), "--checklist-output",
                       str(ROOT / QUALITY / "file-checklist.md")]
            process = subprocess.run(command, cwd=ROOT, capture_output=True, text=True)
            if process.returncode:
                print(process.stderr or process.stdout, file=sys.stderr)
                return process.returncode
            result = checkpoint()
            if args.require_complete and result["summary"]["accepted_files"] != result["summary"]["files"]:
                print(json.dumps(result, ensure_ascii=False, indent=2))
                return 1
        print(json.dumps(result, ensure_ascii=False, indent=2))
        return 0
    except (OSError, ValueError, KeyError, TypeError, ImportError, subprocess.SubprocessError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())
