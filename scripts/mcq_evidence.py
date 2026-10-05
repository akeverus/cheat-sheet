"""Проверяемые результаты по блокам. Сигналы и историческое чтение не дают PASS."""
from __future__ import annotations
from datetime import date, datetime
import hashlib
from pathlib import Path
import re

from mcq_validation import read_json, parse_json

CHECKS = ("content_review", "factual_correctness", "source_linkage", "sections_consistency",
          "sfpl", "ansc", "russian_readability", "independent_review", "blind_review", "final_acceptance")
STATUSES = {"NOT_RUN", "IN_PROGRESS", "PARTIAL", "PASS", "FAIL", "STALE", "BLOCKED", "UNVERIFIED"}
RULE_PATHS = ("docs/mcq-quality-standard.md", "docs/mcq-quality/surface-parity-rules.md",
              "docs/mcq-quality/russian-readability-rules.md")

def sha256(path):
    return hashlib.sha256(Path(path).read_bytes()).hexdigest()

def timestamp(value):
    if not isinstance(value, str):
        raise ValueError("date must be an ISO timestamp")
    try:
        parsed = datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError as exc:
        raise ValueError("invalid timestamp") from exc
    if parsed.tzinfo is None:
        raise ValueError("timestamp requires timezone")
    return parsed

def nonempty(value):
    return isinstance(value, str) and bool(value.strip())

def valid_hash(value):
    return isinstance(value, str) and re.fullmatch(r"[a-f0-9]{64}", value) is not None

def relative_path(value):
    return nonempty(value) and not Path(value).is_absolute() and ".." not in Path(value).parts

def block_id(value):
    return f"Q{value['q_number']}:B{value['block_idx']}"

def validate_record(record):
    required = {"review_id", "reviewer", "author", "model", "date", "kind", "paths", "sha256",
                "rules_sha256", "coverage", "findings", "sources", "limitations"}
    if not isinstance(record, dict) or required - record.keys():
        raise ValueError("missing evidence metadata")
    for key in ("review_id", "reviewer", "author", "kind"):
        if not nonempty(record[key]):
            raise ValueError(f"invalid {key}")
    if record["kind"] not in {"author", "independent", "independent_two_phase", "human"}:
        raise ValueError("unsupported review kind")
    timestamp(record["date"])
    if record["model"] is not None and not nonempty(record["model"]):
        raise ValueError("invalid model")
    paths = record["paths"]
    if not isinstance(paths, dict) or set(paths) != {"mcq", "theory"} or not all(map(relative_path, paths.values())):
        raise ValueError("invalid paths")
    for field in ("sha256", "rules_sha256"):
        value = record[field]
        expected = {"mcq", "theory"} if field == "sha256" else set(RULE_PATHS)
        if not isinstance(value, dict) or set(value) != expected or not all(map(valid_hash, value.values())):
            raise ValueError(f"invalid {field}")
    coverage = record["coverage"]
    if not isinstance(coverage, list) or not coverage:
        raise ValueError("empty coverage")
    seen = set()
    for item in coverage:
        if not isinstance(item, dict) or any(type(item.get(k)) is not int for k in ("q_number", "block_idx")):
            raise ValueError("invalid Q/B")
        if item["q_number"] < 1 or item["block_idx"] < 0 or block_id(item) in seen:
            raise ValueError("invalid/duplicate Q/B")
        seen.add(block_id(item))
        if not isinstance(item.get("sha256"), dict) or set(item["sha256"]) != {"mcq", "theory"} or not all(map(valid_hash, item["sha256"].values())):
            raise ValueError("invalid block hashes")
        checks = item.get("checks")
        if not isinstance(checks, dict) or not checks or set(checks) - set(CHECKS):
            raise ValueError("invalid checks")
        for result in checks.values():
            if not isinstance(result, dict) or result.get("status") not in STATUSES or not nonempty(result.get("reason")):
                raise ValueError("check requires status and reason")
            if result["status"] == "BLOCKED" and not nonempty(result.get("blocker")):
                raise ValueError("BLOCKED requires concrete blocker")
        dependencies = item.get("dependencies", {})
        if not isinstance(dependencies, dict) or not all(relative_path(p) and valid_hash(h) for p, h in dependencies.items()):
            raise ValueError("invalid semantic dependencies")
    for key in ("findings", "sources", "limitations"):
        if not isinstance(record[key], list):
            raise ValueError(f"invalid {key}")
    finding_ids = set()
    for finding in record["findings"]:
        if isinstance(finding, dict) and finding.get("id") in finding_ids:
            raise ValueError("duplicate finding id")
        if isinstance(finding, dict):
            finding_ids.add(finding.get("id"))
        if not isinstance(finding, dict) or not nonempty(finding.get("id")) or finding.get("check") not in CHECKS or finding.get("status") not in {"OPEN", "RESOLVED"}:
            raise ValueError("invalid finding")
        if not isinstance(finding.get("blocks"), list) or not finding["blocks"] or not set(finding["blocks"]).issubset(seen):
            raise ValueError("finding outside coverage")
        if not nonempty(finding.get("reason")):
            raise ValueError("finding requires reason")
    for source in record["sources"]:
        if not isinstance(source, dict) or not all(nonempty(source.get(k)) for k in ("url", "used_for", "version", "checked_at")):
            raise ValueError("source requires checked claim/version/date")
        try:
            date.fromisoformat(source["checked_at"])
        except ValueError:
            try:
                timestamp(source["checked_at"])
            except ValueError as exc:
                raise ValueError("source checked_at must be an ISO date or timestamp") from exc
    return record

def load_records(directory):
    """Пустой каталог допустим, повреждённый evidence останавливает генерацию."""
    result, ids = [], set()
    for path in sorted(Path(directory).glob("*.json")):
        try:
            record = validate_record(read_json(path))
        except (OSError, ValueError, TypeError, KeyError) as exc:
            raise ValueError(f"{path}: {exc}") from exc
        if record["review_id"] in ids:
            raise ValueError(f"duplicate review_id: {record['review_id']}")
        ids.add(record["review_id"])
        record["_evidence_path"] = str(path)
        result.append(record)
    return result

def artifact_current(ref, root):
    return (isinstance(ref, dict) and relative_path(ref.get("path")) and valid_hash(ref.get("sha256"))
            and (root / ref["path"]).is_file() and sha256(root / ref["path"]) == ref["sha256"])

def blind_current(record, root, bid):
    """Фаза A хранит ответы до раскрытия; декларации blind=true недостаточно."""
    ref = record.get("phase_a")
    if record["kind"] != "independent_two_phase" or not artifact_current(ref, root):
        return False
    try:
        phase = read_json(root / ref["path"])
        archive = record.get("blind_input_archive")
        input_ok = artifact_current(phase["input"], root) or (
            artifact_current(archive, root) and archive["sha256"] == phase["input"]["sha256"])
        if not input_ok:
            return False
        input_path = phase["input"]["path"] if artifact_current(phase["input"], root) else archive["path"]
        exported = [parse_json(line) for line in (root / input_path).read_text().splitlines()]
        # Экспорт не должен раскрывать ключ или ссылаться на другой текст того же Q/B.
        if any(set(x) != {"file", "q_number", "block_idx", "visible_question", "options"}
               or any(set(o) != {"label", "text"} for o in x["options"]) for x in exported):
            return False
        exported_ids = [block_id(x) for x in exported]
        if len(set(exported_ids)) != len(exported_ids):
            return False
        answers = phase.get("answers")
        answer_ids = [block_id(a) for a in answers]
        if (len(answer_ids) != len(set(answer_ids)) or set(answer_ids) != set(exported_ids)
                or any(a.get("choice") not in {"A", "B", "C", "D", "AMBIGUOUS", "NO_VALID_OPTION", "UNSURE"}
                       for a in answers)):
            return False
        import mcq_validation as mcq
        data = read_json(root / record["paths"]["mcq"])
        visible = {q["q_number"]: q["title"] for q in mcq.question_sections((root / record["paths"]["theory"]).read_text())}
        candidates = [x for x in exported if block_id(x) == bid]
        if len(candidates) != 1:
            return False
        item = candidates[0]
        block = next(b for q in data["questions"] if q["q_number"] == item["q_number"]
                     for b in q["blocks"] if b["block_idx"] == item["block_idx"])
        expected_options = [{"label": o["label"], "text": o["text"]} for o in block["options"]]
        return (phase["reviewer"] == record["reviewer"] and phase["kind"] == "blind_phase_a"
                and timestamp(phase["recorded_at"]) < timestamp(record["revealed_at"]) <= timestamp(record["date"])
                and item["file"] == record["paths"]["mcq"] and item["visible_question"] == visible[item["q_number"]]
                and item["options"] == expected_options
                and any(block_id(a) == bid for a in answers))
    except (OSError, ValueError, KeyError, TypeError, StopIteration):
        return False

def aggregate(statuses):
    values = list(statuses)
    if not values:
        return "NOT_RUN"
    for priority in ("FAIL", "BLOCKED", "STALE"):
        if priority in values:
            return priority
    if all(s == "PASS" for s in values):
        return "PASS"
    if "PASS" in values or "PARTIAL" in values:
        return "PARTIAL"
    if "IN_PROGRESS" in values:
        return "IN_PROGRESS"
    if "UNVERIFIED" in values:
        return "UNVERIFIED"
    return "NOT_RUN"

def evaluate(row, records, current_block_hashes, current_rules, root=None):
    root = Path(root or ".")
    blocks = row["all_block_ids"]
    states = {c: {b: {"status": "NOT_RUN", "evidence": []} for b in blocks} for c in CHECKS}
    findings = {}
    finding_scopes = {}
    selected = [r for r in records if r["paths"]["mcq"] == row["mcq"]]
    for record in sorted(selected, key=lambda r: (timestamp(r["date"]), r["review_id"])):
        validate_record(record)
        if record["paths"]["theory"] != row["theory"]:
            raise ValueError(f"wrong theory pair: {record['review_id']}")
        if not {block_id(x) for x in record["coverage"]}.issubset(blocks):
            raise ValueError(f"evidence references unknown Q/B: {record['review_id']}")
        ref = record.get("_evidence_path", record["review_id"])
        current = {}
        for item in record["coverage"]:
            bid = block_id(item)
            dependencies = item.get("dependencies", {})
            bound = (record["rules_sha256"] == current_rules and item["sha256"] == current_block_hashes.get(bid)
                     and all((root / p).is_file() and sha256(root / p) == h for p, h in dependencies.items()))
            if record.get("review_artifact") and not artifact_current(record["review_artifact"], root):
                bound = False
            current[bid] = bound
            for check, verdict in item["checks"].items():
                status = verdict["status"] if bound else "STALE"
                independent = record["kind"] in {"independent", "independent_two_phase", "human"} and record["reviewer"] != record["author"]
                if status == "PASS" and check == "independent_review" and not independent:
                    status = "UNVERIFIED"
                if status == "PASS" and check == "blind_review" and (not independent or not blind_current(record, root, bid)):
                    status = "UNVERIFIED"
                if status == "PASS" and check == "factual_correctness" and not record["sources"]:
                    status = "UNVERIFIED"
                states[check][bid] = {"status": status, "reason": verdict["reason"], "evidence": [ref]}
        for finding in record["findings"]:
            scope = (finding["check"], frozenset(finding["blocks"]))
            prior_scope = finding_scopes.setdefault(finding["id"], scope)
            if scope != prior_scope:
                raise ValueError(f"finding id changed: {finding['id']}")
            if finding["status"] == "OPEN":
                findings[finding["id"]] = dict(finding, evidence=ref)
            elif finding["id"] in findings:
                previous = findings[finding["id"]]
                # Resolution действительно перепроверяет ту же проверку всех затронутых блоков.
                if (finding["check"] == previous["check"] and set(previous["blocks"]).issubset(finding["blocks"])
                        and all(current.get(b) and states[finding["check"]][b]["status"] == "PASS"
                                and any(block_id(item) == b and item["checks"].get(finding["check"], {}).get("status") == "PASS"
                                        for item in record["coverage"])
                                for b in previous["blocks"])):
                    del findings[finding["id"]]
    for finding in findings.values():
        for bid in finding["blocks"]:
            states[finding["check"]][bid] = {"status": "FAIL", "reason": finding["reason"], "evidence": [finding["evidence"]]}
    for bid in blocks:
        result = states["final_acceptance"][bid]
        prerequisites = [states[c][bid]["status"] for c in CHECKS[:-1]]
        if row.get("structure") != "PASS":
            result["status"] = "FAIL"
        elif "FAIL" in prerequisites:
            result["status"] = "FAIL"
        elif result["status"] == "PASS" and any(s != "PASS" for s in prerequisites):
            result["status"] = aggregate(prerequisites)
        for issue in row.get("open_issues", []):
            if issue.get("status") in {"RESOLVED", "CLOSED"}:
                continue
            affected = block_id(issue) if "q_number" in issue and "block_idx" in issue else None
            if affected is None or affected == bid:
                result.update(status="BLOCKED", reason=issue.get("next_action", issue.get("id", "open issue")))
        for issue in row.get("runtime_findings", []):
            if issue.get("status") not in {"RESOLVED", "CLOSED"} and (not issue.get("questions") or int(bid.split(':')[0][1:]) in issue["questions"]):
                result.update(status="BLOCKED", reason=issue.get("next_action", issue["id"]))
    checks = {}
    for check, block_states in states.items():
        passed = [b for b in blocks if block_states[b]["status"] == "PASS"]
        checks[check] = {"status": aggregate(s["status"] for s in block_states.values()),
                         "default_block_status": "NOT_RUN",
                         "block_statuses": {b: s for b, s in block_states.items() if s["status"] != "NOT_RUN"},
                         "passed_blocks": passed,
                         "remaining_blocks": [b for b in blocks if b not in passed],
                         "current_blocks": len(passed), "required_blocks": len(blocks),
                         "evidence": sorted({p for s in block_states.values() for p in s["evidence"]}),
                         "open_findings": [f for f in findings.values() if f["check"] == check]}
    return checks
